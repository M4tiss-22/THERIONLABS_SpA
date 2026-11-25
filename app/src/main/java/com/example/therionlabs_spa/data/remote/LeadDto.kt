package com.example.therionlabs_spa.data.remote

import com.example.therionlabs_spa.data.local.LeadEntity


data class LeadDto(
    val idLocal: Long,
    val nombre: String,
    val email: String,
    val telefono: String,
    val nivelEducacion: String,
    val cursoInteres: String,
    val modalidad: String,
    val canalContacto: String,
    val observaciones: String,
    val fechaCaptura: Long
)

fun LeadEntity.toDto(): LeadDto = LeadDto(
    idLocal = id,
    nombre = nombre,
    email = email,
    telefono = telefono,
    nivelEducacion = nivelEducacion,
    cursoInteres = cursoInteres,
    modalidad = modalidad,
    canalContacto = canalContacto,
    observaciones = observaciones,
    fechaCaptura = fechaCaptura
)
