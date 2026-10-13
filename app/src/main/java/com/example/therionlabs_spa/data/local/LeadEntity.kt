package com.example.therionlabs_spa.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "Prospectos")
data class LeadEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nombre: String,
    val email: String,
    val telefono: String,
    val nivelEducacion: String,
    val cursoInteres : String,
    val modalidad : String,
    val canalContacto : String,
    val observaciones : String = "",
    val fechaCaptura : Long = System.currentTimeMillis(),
    val estaSincronizado : Boolean = false
)

