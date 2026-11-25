package com.example.therionlabs_spa.data.repository

import com.example.therionlabs_spa.data.local.LeadDao
import com.example.therionlabs_spa.data.local.LeadEntity
import com.example.therionlabs_spa.data.remote.LeadApiService
import com.example.therionlabs_spa.data.remote.toDto
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow

data class ResultadoSincronizacion(val enviados: Int, val fallidos: Int)


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

                fallidos++
            }
        }
        return ResultadoSincronizacion(enviados, fallidos)
    }
}
