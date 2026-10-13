package com.example.therionlabs_spa.ui.viewmodel


import android.app.Notification
import android.graphics.Bitmap
import androidx.compose.ui.layout.AlignmentLine

data class LeadFormUiState(

    //Este es el formulario de LeadEntity cabros x si acaso
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
    val estaSincronizado : Boolean = false,

    //Cabros aca estos son mensajes de error para el usuario de acuerdo a validationUtils
    val errorNombre : String? = null,
    val errorEmail : String? = null,
    val errorTelefono : String? = null,

    // Y esto es para el control del UI
    val formularioValido : Boolean = false,
    val estaGuardado : Boolean = false,
    val estaEnLinea : Boolean = false,
    val mensajeNotification: String? = null
)
