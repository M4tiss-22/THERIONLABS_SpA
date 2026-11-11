package com.example.therionlabs_spa.ui.viewmodel

import com.example.therionlabs_spa.data.local.LeadDao
import com.example.therionlabs_spa.data.local.LeadEntity
import com.example.therionlabs_spa.data.remote.LeadApiService
import com.example.therionlabs_spa.data.remote.LeadDto
import com.example.therionlabs_spa.data.repository.LeadRepository
import com.example.therionlabs_spa.util.EstadoRed
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import java.io.IOException

// ---------- Dobles de prueba (sin Android, sin red, sin base real) ----------

private class FakeLeadDao : LeadDao {
    val prospectos = MutableStateFlow<List<LeadEntity>>(emptyList())
    var fallarAlInsertar = false
    private var siguienteId = 1L

    override suspend fun insertarProspecto(prospecto: LeadEntity): Long {
        if (fallarAlInsertar) throw IllegalStateException("fallo simulado de la base")
        val id = siguienteId++
        prospectos.update { it + prospecto.copy(id = id) }
        return id
    }

    override fun obtenerTodosLosProspectos(): Flow<List<LeadEntity>> = prospectos

    override fun obtenerCantidadPendientes(): Flow<Int> =
        prospectos.map { lista -> lista.count { !it.estaSincronizado } }

    override suspend fun obtenerPendientes(): List<LeadEntity> =
        prospectos.value.filter { !it.estaSincronizado }

    override suspend fun marcarSincronizado(id: Long) {
        prospectos.update { lista ->
            lista.map { if (it.id == id) it.copy(estaSincronizado = true) else it }
        }
    }
}

private class FakeApi : LeadApiService {
    val recibidos = mutableListOf<LeadDto>()
    var codigoRespuesta = 201
    var lanzarErrorDeRed = false
    var compuerta: CompletableDeferred<Unit>? = null

    override suspend fun enviarProspecto(prospecto: LeadDto): Response<Unit> {
        compuerta?.await()
        recibidos += prospecto
        if (lanzarErrorDeRed) throw IOException("sin conexión")
        return if (codigoRespuesta < 400) Response.success(Unit)
        else Response.error<Unit>(codigoRespuesta, "".toResponseBody(null))
    }
}

private class FakeRed(inicial: Boolean) : EstadoRed {
    val conectado = MutableStateFlow(inicial)
    override val estaConectado: Flow<Boolean> = conectado
}

// ---------- Pruebas ----------

@OptIn(ExperimentalCoroutinesApi::class)
class LeadFormViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()
    private lateinit var dao: FakeLeadDao
    private lateinit var api: FakeApi
    private lateinit var red: FakeRed

    @Before
    fun preparar() {
        Dispatchers.setMain(dispatcher)
        dao = FakeLeadDao()
        api = FakeApi()
        red = FakeRed(inicial = false)
    }

    @After
    fun limpiar() {
        Dispatchers.resetMain()
    }

    private fun crearViewModel() = LeadFormViewModel(LeadRepository(dao, api), red)

    private fun LeadFormViewModel.llenarFormularioValido() {
        onNombreCambiado("Juan Pérez")
        onEmailCambiado("juan@duocuc.cl")
        onTelefonoCambiado("912345678")
    }

    // ----- estado inicial y red -----

    @Test
    fun estadoInicial_esFormularioVacioSinConexion() {
        val estado = crearViewModel().estadoUi.value

        assertEquals("", estado.nombre)
        assertEquals("", estado.email)
        assertEquals("", estado.telefono)
        assertFalse(estado.formularioValido)
        assertFalse(estado.estaEnLinea)
        assertFalse(estado.estaSincronizando)
        assertNull(estado.mensajeNotificacion)
    }

    @Test
    fun monitorDeRed_actualizaElEstadoEnLinea() {
        val vm = crearViewModel()

        red.conectado.value = true
        assertTrue(vm.estadoUi.value.estaEnLinea)

        red.conectado.value = false
        assertFalse(vm.estadoUi.value.estaEnLinea)
    }

    // ----- validación de campos -----

    @Test
    fun nombreInvalido_muestraErrorYFormularioInvalido() {
        val vm = crearViewModel()

        vm.onNombreCambiado("ab")

        assertNotNull(vm.estadoUi.value.errorNombre)
        assertFalse(vm.estadoUi.value.formularioValido)
    }

    @Test
    fun nombreValido_limpiaElError() {
        val vm = crearViewModel()

        vm.onNombreCambiado("ab")
        vm.onNombreCambiado("Juan Pérez")

        assertNull(vm.estadoUi.value.errorNombre)
        assertEquals("Juan Pérez", vm.estadoUi.value.nombre)
    }

    @Test
    fun emailInvalido_muestraErrorYFormularioInvalido() {
        val vm = crearViewModel()

        vm.onEmailCambiado("correo-malo")

        assertNotNull(vm.estadoUi.value.errorEmail)
        assertFalse(vm.estadoUi.value.formularioValido)
    }

    @Test
    fun emailValido_sinNombreNiTelefono_noHabilitaElFormulario() {
        val vm = crearViewModel()

        vm.onEmailCambiado("juan@duocuc.cl")

        assertNull(vm.estadoUi.value.errorEmail)
        assertFalse(vm.estadoUi.value.formularioValido)
    }

    @Test
    fun emailValido_conNombreYTelefonoValidos_habilitaElFormulario() {
        val vm = crearViewModel()

        vm.onNombreCambiado("Juan Pérez")
        vm.onTelefonoCambiado("912345678")
        vm.onEmailCambiado("juan@duocuc.cl")

        assertTrue(vm.estadoUi.value.formularioValido)
    }

    @Test
    fun telefonoInvalido_muestraErrorYFormularioInvalido() {
        val vm = crearViewModel()

        vm.onTelefonoCambiado("123")

        assertNotNull(vm.estadoUi.value.errorTelefono)
        assertFalse(vm.estadoUi.value.formularioValido)
    }

    @Test
    fun telefonoDeNueveDigitos_esValido() {
        val vm = crearViewModel()

        vm.onTelefonoCambiado("912345678")

        assertNull(vm.estadoUi.value.errorTelefono)
    }

    @Test
    fun conTodosLosCamposValidos_elFormularioQuedaValido() {
        val vm = crearViewModel()

        vm.llenarFormularioValido()

        assertTrue(vm.estadoUi.value.formularioValido)
    }

    @Test
    fun cursoObservacionesYFoto_seActualizanEnElEstado() {
        val vm = crearViewModel()

        vm.onCursoSeleccionado("Técnico en Redes")
        vm.onObservacionesCambiadas("Llamar mañana")
        vm.onFotoCapturada(null)

        assertEquals("Técnico en Redes", vm.estadoUi.value.cursoInteres)
        assertEquals("Llamar mañana", vm.estadoUi.value.observaciones)
        assertNull(vm.estadoUi.value.fotoCapturada)
    }

    // ----- guardado local -----

    @Test
    fun guardar_conFormularioInvalido_noInsertaNada() {
        val vm = crearViewModel()

        vm.guardarProspecto()

        assertTrue(dao.prospectos.value.isEmpty())
        assertNull(vm.estadoUi.value.mensajeNotificacion)
    }

    @Test
    fun guardar_sinConexion_guardaEnLocalYQuedaPendiente() {
        val vm = crearViewModel()
        vm.llenarFormularioValido()
        vm.onObservacionesCambiadas("  interesado en Ingeniería  ")

        vm.guardarProspecto()

        val guardados = dao.prospectos.value
        assertEquals(1, guardados.size)
        assertEquals("Juan Pérez", guardados[0].nombre)
        assertEquals("interesado en Ingeniería", guardados[0].observaciones)
        assertFalse(guardados[0].estaSincronizado)
        assertTrue(api.recibidos.isEmpty())

        val estado = vm.estadoUi.value
        assertEquals("", estado.nombre)
        assertFalse(estado.formularioValido)
        assertFalse(estado.estaGuardando)
        assertEquals("¡Prospecto guardado exitosamente en base local!", estado.mensajeNotificacion)
    }

    @Test
    fun guardar_conConexion_loEnviaAlServidorYLoMarcaSincronizado() {
        val vm = crearViewModel()
        red.conectado.value = true
        vm.llenarFormularioValido()

        vm.guardarProspecto()

        assertEquals(1, api.recibidos.size)
        assertEquals("juan@duocuc.cl", api.recibidos[0].email)
        assertTrue(dao.prospectos.value[0].estaSincronizado)
        // El envío automático no pisa el aviso de "guardado".
        assertEquals("¡Prospecto guardado exitosamente en base local!", vm.estadoUi.value.mensajeNotificacion)
    }

    @Test
    fun guardar_cuandoFallaLaBase_muestraErrorYConservaLosDatos() {
        dao.fallarAlInsertar = true
        val vm = crearViewModel()
        vm.llenarFormularioValido()

        vm.guardarProspecto()

        val estado = vm.estadoUi.value
        assertEquals("Error al guardar el prospecto en base local", estado.mensajeNotificacion)
        assertFalse(estado.estaGuardando)
        assertEquals("Juan Pérez", estado.nombre)
        assertTrue(dao.prospectos.value.isEmpty())
    }

    @Test
    fun limpiarNotificacion_borraElMensaje() {
        val vm = crearViewModel()
        vm.llenarFormularioValido()
        vm.guardarProspecto()
        assertNotNull(vm.estadoUi.value.mensajeNotificacion)

        vm.limpiarNotificacion()

        assertNull(vm.estadoUi.value.mensajeNotificacion)
    }

    // ----- sincronización con el backend -----

    @Test
    fun sincronizar_sinPendientes_avisaQueNoHayNada() {
        val vm = crearViewModel()

        vm.sincronizarProspectos()

        assertEquals("No hay prospectos pendientes por sincronizar", vm.estadoUi.value.mensajeNotificacion)
        assertFalse(vm.estadoUi.value.estaSincronizando)
    }

    @Test
    fun sincronizar_conServidorOk_marcaLosProspectosComoSincronizados() {
        val vm = crearViewModel()
        vm.llenarFormularioValido()
        vm.guardarProspecto()
        vm.limpiarNotificacion()

        vm.sincronizarProspectos()

        assertEquals(1, api.recibidos.size)
        assertTrue(dao.prospectos.value[0].estaSincronizado)
        assertEquals("1 prospecto(s) sincronizado(s) con el servidor", vm.estadoUi.value.mensajeNotificacion)
        assertFalse(vm.estadoUi.value.estaSincronizando)
    }

    @Test
    fun sincronizar_siElServidorRechaza_elProspectoSiguePendiente() {
        api.codigoRespuesta = 500
        val vm = crearViewModel()
        vm.llenarFormularioValido()
        vm.guardarProspecto()
        vm.limpiarNotificacion()

        vm.sincronizarProspectos()

        assertFalse(dao.prospectos.value[0].estaSincronizado)
        assertEquals(
            "Sincronizados 0; 1 siguen pendientes (revisa tu conexión)",
            vm.estadoUi.value.mensajeNotificacion
        )
        assertFalse(vm.estadoUi.value.estaSincronizando)
    }

    @Test
    fun sincronizar_conErrorDeRed_elProspectoSigueSiendoPendiente() {
        api.lanzarErrorDeRed = true
        val vm = crearViewModel()
        vm.llenarFormularioValido()
        vm.guardarProspecto()
        vm.limpiarNotificacion()

        vm.sincronizarProspectos()

        assertFalse(dao.prospectos.value[0].estaSincronizado)
        assertEquals(
            "Sincronizados 0; 1 siguen pendientes (revisa tu conexión)",
            vm.estadoUi.value.mensajeNotificacion
        )
    }

    @Test
    fun sincronizar_mientrasOtraEstaEnCurso_noEnviaDuplicados() {
        val vm = crearViewModel()
        vm.llenarFormularioValido()
        vm.guardarProspecto()
        api.compuerta = CompletableDeferred()

        vm.sincronizarProspectos()
        assertTrue(vm.estadoUi.value.estaSincronizando)
        vm.sincronizarProspectos() // se ignora: ya hay una en curso
        api.compuerta!!.complete(Unit)

        assertEquals(1, api.recibidos.size)
        assertFalse(vm.estadoUi.value.estaSincronizando)
    }

    @Test
    fun alRecuperarConexion_enviaLosPendientesAutomaticamente() {
        val vm = crearViewModel()
        vm.llenarFormularioValido()
        vm.guardarProspecto()
        assertTrue(api.recibidos.isEmpty())

        red.conectado.value = true

        assertEquals(1, api.recibidos.size)
        assertTrue(dao.prospectos.value[0].estaSincronizado)
        assertTrue(vm.estadoUi.value.estaEnLinea)
    }

    // ----- flujos expuestos a la UI -----

    @Test
    fun listaYPendientes_reflejanLaBaseLocal() = runTest {
        val vm = crearViewModel()
        backgroundScope.launch(dispatcher) { vm.listaProspectos.collect { } }
        backgroundScope.launch(dispatcher) { vm.totalPendientes.collect { } }
        assertEquals(0, vm.totalPendientes.value)

        vm.llenarFormularioValido()
        vm.guardarProspecto()
        assertEquals(1, vm.listaProspectos.value.size)
        assertEquals(1, vm.totalPendientes.value)

        red.conectado.value = true
        assertEquals(0, vm.totalPendientes.value)
    }
}
