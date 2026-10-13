package com.example.therionlabs_spa.ui.viewmodel


object ValidationUtils {
    fun validarNombre(nombre: String): String? {
        return when {
            nombre.isBlank() -> "El nombre es Obligatorio."
            nombre.trim().length < 3 -> "El nombre debe tener al menos 3 caracteres."
            else -> null
        }
    }

    fun validarEmail(email: String): String? {
        val expresionEmail = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\\\.[a-zA-Z]{2,}\\\$".toRegex()
        return when {
            email.isBlank() -> "El correo electrónico es Obligatorio."
            !expresionEmail.matches(email) -> "El correo electrónico no es válido."
            else -> null
        }
    }

    fun validadTelefono(telefono: String): String? {
        val expresionTelefono = "^[0-9]{10}\$".toRegex()
        return when {
            telefono.isBlank() -> "El teléfono es Obligatorio."
            !telefono.matches(expresionTelefono) -> "El teléfono debe tener 9 digitos."
            else -> null
        }
    }
}