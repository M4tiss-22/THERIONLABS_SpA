package com.example.therionlabs_spa.ui.screens

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.therionlabs_spa.ui.components.ValidatedTextField
import com.example.therionlabs_spa.ui.viewmodel.LeadFormViewModel

@Optin(ExperimentalMaterial3Api::class)
@Composable
fun LeadFormScreen(
    viewModel: LeadFormViewModel,
    irALista: () -> Unit
) {
    val estado by viewModel.estadoUi.collectAsState()
    val estadoNotificacion = remember { SnackbarHostState() }


    val lanzadorCamara = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { fotoTomada: Bitmap? ->
        viewModel.onFotoCapturada(fotoTomada)
    }


    LaunchedEffect(estado.mensajeNotificacion) {
        estado.mensajeNotificacion?.let { mensaje ->
            estadoNotificacion.showSnackbar(mensaje)
            viewModel.limpiarNotificacion()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("THERIONLABS - Captura") },
                actions = {
                    IconButton(onClick = irALista) {
                        Icon(Icons.Default.List, contentDescription = "Ver listado de prospectos")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(estadoNotificacion) }
    ) { relleno ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(relleno)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Surface(
                color = if (estado.estaEnLinea) MaterialTheme.colorScheme.primaryContainer
                else MaterialTheme.colorScheme.errorContainer,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        if (estado.estaEnLinea) Icons.Default.Wifi else Icons.Default.WifiOff,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (estado.estaEnLinea) "En línea: Sincronización activa"
                        else "Sin conexión: Operando en persistencia local",
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }

            Text(
                text = "Formulario de Registro en Terreno",
                style = MaterialTheme.typography.titleMedium
            )


            ValidatedTextField(
                valor = estado.nombre,
                alCambiarValor = { viewModel.onNombreCambiado(it) },
                etiqueta = "Nombre Completo *",
                iconoPrincipal = Icons.Default.Person,
                mensajeError = estado.errorNombre
            )

            ValidatedTextField(
                valor = estado.email,
                alCambiarValor = { viewModel.onEmailCambiado(it) },
                etiqueta = "Correo Electrónico *",
                iconoPrincipal = Icons.Default.Email,
                mensajeError = estado.errorEmail,
                opcionesTeclado = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            ValidatedTextField(
                valor = estado.telefono,
                alCambiarValor = { viewModel.onTelefonoCambiado(it) },
                etiqueta = "Teléfono de Contacto (9 dígitos) *",
                iconoPrincipal = Icons.Default.Phone,
                mensajeError = estado.errorTelefono,
                opcionesTeclado = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )

            OutlinedTextField(
                value = estado.cursoInteres,
                onValueChange = { viewModel.onCursoSeleccionado(it) },
                label = { Text("Curso / Especialización") },
                leadingIcon = { Icon(Icons.Default.School, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = estado.observaciones,
                onValueChange = { viewModel.onObservacionesCambiadas(it) },
                label = { Text("Observaciones adicionales") },
                leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(onClick = { lanzadorCamara.launch() }) {
                    Icon(Icons.Default.PhotoCamera, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Fotografiar Credencial")
                }

                estado.fotoCapturada?.let { miniatura ->
                    Image(
                        bitmap = miniatura.asImageBitmap(),
                        contentDescription = "Credencial capturada",
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Button(
                onClick = { viewModel.guardarProspecto() },
                enabled = estado.formularioValido && !estado.estaGuardando,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                if (estado.estaGuardando) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Icon(Icons.Default.Save, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Guardar en Base de Datos Local")
                }
            }
        }
    }
}
