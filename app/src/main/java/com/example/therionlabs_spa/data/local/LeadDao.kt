package com.example.therionlabs_spa.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LeadDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarProspecto(prospecto: LeadEntity): Long

    @Query("SELECT * FROM prospectos ORDER BY fechaCaptura DESC")
    fun obtenerTodosLosProspectos(): Flow<List<LeadEntity>>

    @Query("SELECT COUNT(*) FROM prospectos WHERE estaSincronizado = 0")
    fun obtenerCantidadPendientes(): Flow<Int>

    @Query("UPDATE Prospectos SET estaSincronizado = 1 WHERE estaSincronizado = 0")
    suspend fun sincronizarTodos()
}