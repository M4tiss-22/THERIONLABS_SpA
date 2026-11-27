package com.example.therionlabs_spa.ui.viewmodel

import android.graphics.Bitmap

data class LeadFormUiState(
    val nombre: String = "",
    val email: String = "",
    val telefono: String = "",
    val nivelEducacion: String = "Técnico Profesional",
    val cursoInteres: String = "Ingeniería en informatica",
    val modalidad: String = "Online",
    val canalContacto: String = "WhatsApp",
    val observaciones: String = "",
    val fotoCapturada: Bitmap? = null,
    val errorNombre: String? = null,
    val errorEmail: String? = null,
    val errorTelefono: String? = null,
    val formularioValido: Boolean = false,
    val estaGuardando: Boolean = false,
    val estaEnLinea: Boolean = false,
    val estaSincronizando: Boolean = false,
    val mensajeNotificacion: String? = null
)

