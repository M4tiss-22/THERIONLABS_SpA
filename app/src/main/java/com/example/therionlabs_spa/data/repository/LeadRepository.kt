package com.example.therionlabs_spa.data.repository

import com.example.therionlabs_spa.data.local.LeadDao
import com.example.therionlabs_spa.data.local.LeadEntity
import com.example.therionlabs_spa.data.remote.LeadApiService
import com.example.therionlabs_spa.data.remote.toDto
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow

/** Resultado de un intento de sincronización: cuántos prospectos llegaron y cuántos quedaron pendientes. */
data class ResultadoSincronizacion(val enviados: Int, val fallidos: Int)

/**
 * Local (Room): captura, cola de pendientes y listado. Es la fuente de verdad en terreno.
 * Remoto (API): recepción final de los prospectos. Un prospecto solo se marca como
 * sincronizado cuando el backend responde 2xx; si falla, queda pendiente para reintentar.
 */
class LeadRepository(
    private val dao: LeadDao,
    private val api: LeadApiService
) {
    val todosLosProspectos: Flow<List<LeadEntity>> = dao.obtenerTodosLosProspectos()
    val cantidadPendientes: Flow<Int> = dao.obtenerCantidadPendientes()

    suspend fun guardarProspecto(prospecto: LeadEntity): Boolean {
        return try {
            dao.insertarProspecto(prospecto) > 0
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            false
        }
    }

    suspend fun sincronizarProspectos(): ResultadoSincronizacion {
        var enviados = 0
        var fallidos = 0

        for (prospecto in dao.obtenerPendientes()) {
            try {
                val respuesta = api.enviarProspecto(prospecto.toDto())
                if (respuesta.isSuccessful) {
                    dao.marcarSincronizado(prospecto.id)
                    enviados++
                } else {
                    fallidos++
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                // Sin red, timeout o error de parseo: el prospecto sigue pendiente.
                fallidos++
            }
        }
        return ResultadoSincronizacion(enviados, fallidos)
    }
}
