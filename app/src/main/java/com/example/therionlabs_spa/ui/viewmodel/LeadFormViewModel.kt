package com.example.therionlabs_spa.ui.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.therionlabs_spa.data.local.LeadEntity
import com.example.therionlabs_spa.data.repository.LeadRepository
import com.example.therionlabs_spa.data.repository.ResultadoSincronizacion
import com.example.therionlabs_spa.util.EstadoRed
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class LeadFormViewModel(
    private val repositorio: LeadRepository,
    private val monitorRed: EstadoRed
) : ViewModel() {

    private val _estadoUi = MutableStateFlow(LeadFormUiState())
    val estadoUi: StateFlow<LeadFormUiState> = _estadoUi.asStateFlow()


    val listaProspectos: StateFlow<List<LeadEntity>> = repositorio.todosLosProspectos
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalPendientes: StateFlow<Int> = repositorio.cantidadPendientes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    init {
        viewModelScope.launch {
            monitorRed.estaConectado.distinctUntilChanged().collect { conectado ->
                _estadoUi.update { it.copy(estaEnLinea = conectado) }
                // Al recuperar conexión se envían en segundo plano los prospectos pendientes.
                if (conectado) sincronizarProspectos(mostrarResultado = false)
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
                        estaEnLinea = it.estaEnLinea,
                        estaSincronizando = it.estaSincronizando,
                        mensajeNotificacion = "¡Prospecto guardado exitosamente en base local!"
                    )
                }
                // Con conexión se intenta enviar de inmediato; sin ella queda pendiente.
                if (_estadoUi.value.estaEnLinea) sincronizarProspectos(mostrarResultado = false)
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

    /**
     * Envía al backend los prospectos pendientes. Si ya hay una sincronización en curso no lanza otra.
     * [mostrarResultado] = false para los envíos automáticos (no molestan al usuario con avisos).
     */
    fun sincronizarProspectos(mostrarResultado: Boolean = true) {
        if (_estadoUi.value.estaSincronizando) return
        _estadoUi.update { it.copy(estaSincronizando = true) }

        viewModelScope.launch {
            val resultado = repositorio.sincronizarProspectos()
            val mensaje = if (mostrarResultado) mensajeDeSincronizacion(resultado) else null
            _estadoUi.update {
                it.copy(
                    estaSincronizando = false,
                    mensajeNotificacion = mensaje ?: it.mensajeNotificacion
                )
            }
        }
    }

    private fun mensajeDeSincronizacion(resultado: ResultadoSincronizacion): String = when {
        resultado.enviados == 0 && resultado.fallidos == 0 ->
            "No hay prospectos pendientes por sincronizar"
        resultado.fallidos > 0 ->
            "Sincronizados ${resultado.enviados}; ${resultado.fallidos} siguen pendientes (revisa tu conexión)"
        else -> "${resultado.enviados} prospecto(s) sincronizado(s) con el servidor"
    }
}