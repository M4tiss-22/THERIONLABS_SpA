package com.example.therionlabs_spa.data.repository

import com.example.therionlabs_spa.data.local.LeadDao
import com.example.therionlabs_spa.data.local.LeadEntity
import kotlinx.coroutines.flow.Flow

class LeadRepository(private val dao: LeadDao) {
    val todosLosProspectos: Flow<List<LeadEntity>> = dao.obtenerTodosLosProspectos()
    val cantidadPendientes: Flow<Int> = dao.obtenerCantidadPendientes()

    suspend fun guardarProspecto(prospecto: LeadEntity): Boolean {
        return dao.insertarProspecto(prospecto) > 0
    }
    suspend fun sincronizarProspectos() = dao.sincronizarTodos()
}