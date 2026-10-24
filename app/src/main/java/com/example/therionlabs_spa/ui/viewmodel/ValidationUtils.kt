package com.example.therionlabs_spa.ui.viewmodel

object ValidationUtils {

    fun validarNombre(nombre: String): String? {
        val textoLimpio = nombre.trim()
        return when {
            textoLimpio.isEmpty() -> "El nombre completo es obligatorio"
            textoLimpio.length < 3 -> "El nombre debe tener al menos 3 caracteres"
            else -> null
        }
    }

    fun validarEmail(email: String): String? {
        val textoLimpio = email.trim()
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$".toRegex()

        return when {
            textoLimpio.isEmpty() -> "El correo electrónico es obligatorio"
            !textoLimpio.matches(emailRegex) -> "Formato de correo electrónico no válido"
            else -> null
        }
    }

    fun validarTelefono(telefono: String): String? {
        val textoLimpio = telefono.trim()
        val telefonoRegex = "^[0-9]{9}\$".toRegex()

        return when {
            textoLimpio.isEmpty() -> "El teléfono de contacto es obligatorio"
            !textoLimpio.matches(telefonoRegex) -> "Debe ingresar un número de 9 dígitos numéricos"
            else -> null
        }
    }

    fun esFormularioValido(nombre: String, email: String, telefono: String): Boolean {
        return validarNombre(nombre) == null &&
                validarEmail(email) == null &&
                validarTelefono(telefono) == null
    }
}