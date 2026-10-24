package com.example.therionlabs_spa.ui.viewmodel


import android.app.Notification
import android.graphics.Bitmap
import androidx.compose.ui.layout.AlignmentLine

data class LeadFormUiState(

    //Este es el formulario de LeadEntity cabros x si acaso

    val nombre: String = "",
    val email: String = "",
    val telefono: String = "",
    val nivelEducacion: String = "Técnico Profesional",
    val cursoInteres: String = "Ingeniería en informatica",
    val modalidad: String = "Online",
    val canalContacto: String = "WhatsApp",
    val observaciones: String = "",
    val fotoCapturada: Bitmap? = null,

    //Cabros aca estos son mensajes de error para el usuario de acuerdo a validationUtils
    val errorNombre: String? = null,
    val errorEmail: String? = null,
    val errorTelefono: String? = null,

    // Y esto es para el control del UI
    val formularioValido: Boolean = false,
    val estaGuardando: Boolean = false,
    val estaEnLinea: Boolean = false,
    val mensajeNotificacion: String? = null
)
