package com.example.therionlabs_spa.ui.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.therionlabs_spa.data.local.LeadEntity
import com.example.therionlabs_spa.data.repository.LeadRepository
import com.example.therionlabs_spa.util.NetworkMonitor
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class LeadFormViewModel(
    private val repositorio: LeadRepository,
    private val monitorRed: NetworkMonitor
) : ViewModel() {

    private val _estadoUi = MutableStateFlow(LeadFormUiState())
    val estadoUi: StateFlow<LeadFormUiState> = _estadoUi.asStateFlow()


    val listaProspectos: StateFlow<List<LeadEntity>> = repositorio.todosLosProspectos
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalPendientes: StateFlow<Int> = repositorio.cantidadPendientes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    init {
        viewModelScope.launch {
            monitorRed.estaConectado.collect { conectado ->
                _estadoUi.update { it.copy(estaEnLinea = conectado) }
            }
        }
    }

    fun onNombreCambiado(nuevoNombre: String) {
        val error = ValidationUtils.validarNombre(nuevoNombre)
        _estadoUi.update { it.copy(nombre = nuevoNombre, errorNombre = error) }
        verificarValidezFormulario()
    }

    fun onEmailCambiado(nuevoEmail: String) {
        val error = ValidationUtils.validarEmail(nuevoEmail)
        _estadoUi.update { actual ->
            val proximoEmailValido = error == null && nuevoEmail.isNotBlank()
            val nombreValido = actual.errorNombre == null && actual.nombre.isNotBlank()
            val telefonoValido = actual.errorTelefono == null && actual.telefono.isNotBlank()

            actual.copy(
                email = nuevoEmail,
                errorEmail = error,
                formularioValido = proximoEmailValido && nombreValido && telefonoValido
            )
        }
    }

    fun onTelefonoCambiado(nuevoTelefono: String) {
        val error = ValidationUtils.validarTelefono(nuevoTelefono)
        _estadoUi.update { it.copy(telefono = nuevoTelefono, errorTelefono = error) }
        verificarValidezFormulario()
    }

    fun onCursoSeleccionado(nuevoCurso: String) {
        _estadoUi.update { it.copy(cursoInteres = nuevoCurso) }
    }

    fun onObservacionesCambiadas(texto: String) {
        _estadoUi.update { it.copy(observaciones = texto) }
    }

    fun onFotoCapturada(foto: Bitmap?) {
        _estadoUi.update { it.copy(fotoCapturada = foto) }
    }

    private fun verificarValidezFormulario() {
        val actual = _estadoUi.value
        val esValido = actual.errorNombre == null && actual.nombre.isNotBlank() &&
                actual.errorEmail == null && actual.email.isNotBlank() &&
                actual.errorTelefono == null && actual.telefono.isNotBlank()
        _estadoUi.update { it.copy(formularioValido = esValido) }
    }

    fun guardarProspecto() {
        if (!_estadoUi.value.formularioValido) return

        viewModelScope.launch {
            _estadoUi.update { it.copy(estaGuardando = true) }
            val actual = _estadoUi.value

            val nuevoProspecto = LeadEntity(
                nombre = actual.nombre.trim(),
                email = actual.email.trim(),
                telefono = actual.telefono.trim(),
                nivelEducacion = actual.nivelEducacion,
                cursoInteres = actual.cursoInteres,
                modalidad = actual.modalidad,
                canalContacto = actual.canalContacto,
                observaciones = actual.observaciones.trim(),
                estaSincronizado = false
            )

            val guardadoExitoso = repositorio.guardarProspecto(nuevoProspecto)
            if (guardadoExitoso) {
                _estadoUi.update {
                    LeadFormUiState(
                        estaEnLinea = actual.estaEnLinea,
                        mensajeNotificacion = "¡Prospecto guardado exitosamente en base local!"
                    )
                }
            } else {
                _estadoUi.update {
                    it.copy(
                        estaGuardando = false,
                        mensajeNotificacion = "Error al guardar el prospecto en base local"
                    )
                }
            }
        }
    }

    fun limpiarNotificacion() {
        _estadoUi.update { it.copy(mensajeNotificacion = null) }
    }

    fun sincronizarProspectos() {
        viewModelScope.launch {
            repositorio.sincronizarProspectos()
        }
    }
}