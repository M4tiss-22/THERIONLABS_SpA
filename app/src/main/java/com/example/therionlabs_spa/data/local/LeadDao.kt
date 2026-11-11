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

    @Query("SELECT * FROM Prospectos ORDER BY fechaCaptura DESC")
    fun obtenerTodosLosProspectos(): Flow<List<LeadEntity>>

    @Query("SELECT COUNT(*) FROM Prospectos WHERE estaSincronizado = 0")
    fun obtenerCantidadPendientes(): Flow<Int>

    /** Prospectos que aún no llegan al backend, del más antiguo al más nuevo. */
    @Query("SELECT * FROM Prospectos WHERE estaSincronizado = 0 ORDER BY fechaCaptura ASC")
    suspend fun obtenerPendientes(): List<LeadEntity>

    /** Se llama solo cuando el backend confirmó la recepción de ese prospecto. */
    @Query("UPDATE Prospectos SET estaSincronizado = 1 WHERE id = :id")
    suspend fun marcarSincronizado(id: Long)
}
