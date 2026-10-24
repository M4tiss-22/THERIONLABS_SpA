package com.example.therionlabs_spa.ui.screens

import android.Manifest
import android.graphics.Bitmap
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.therionlabs_spa.R
import com.example.therionlabs_spa.ui.components.ValidatedTextField
import com.example.therionlabs_spa.ui.viewmodel.LeadFormViewModel

private val DuocNavyDark = Color(0xFF0F172A)
private val DuocCardSurface = Color(0xFF1E293B)
private val DuocYellow = Color(0xFFFFAE00)
private val NeonGreen = Color(0xFF10B981)
private val TextLight = Color(0xFFF8FAFC)
private val TextMuted = Color(0xFF94A3B8)
private val BorderSubtle = Color(0xFF334155)

@Composable
fun LeadFormScreen(
    viewModel: LeadFormViewModel,
    onNavigateToLista: () -> Unit,
    onTomarFoto: () -> Unit = {},
    fotoCapturada: Bitmap? = null
) {
    val uiState by viewModel.estadoUi.collectAsState()
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    var fotoLocal by remember { mutableStateOf<Bitmap?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            fotoLocal = bitmap
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            try {
                cameraLauncher.launch()
            } catch (e: Exception) {
                Toast.makeText(context, "No se pudo abrir la cámara", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        containerColor = DuocNavyDark,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DuocNavyDark)
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "THERIONLABS",
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp,
                        color = TextLight
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Captura",
                        fontWeight = FontWeight.Light,
                        fontSize = 18.sp,
                        color = DuocYellow
                    )
                }

                IconButton(
                    onClick = onNavigateToLista,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(DuocCardSurface)
                ) {
                    Icon(
                        imageVector = Icons.Default.List,
                        contentDescription = "Ver Registros",
                        tint = DuocYellow
                    )
                }
            }
        }
    ) { paddingValores ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValores)
                .padding(horizontal = 20.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(
                                Color(0xFF001E4D),
                                DuocCardSurface
                            )
                        )
                    )
                    .border(1.dp, BorderSubtle, RoundedCornerShape(20.dp))
                    .padding(18.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo_duoc),
                        contentDescription = "Insignia Duoc UC",
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                    )

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = "DUOC UC",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp,
                            color = TextLight,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Escuela de Informática y Telecomunicaciones",
                            fontSize = 12.sp,
                            color = DuocYellow,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Desarrollo Móvil Android",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }
                }
            }

            Surface(
                shape = RoundedCornerShape(14.dp),
                color = DuocCardSurface,
                border = BorderStroke(
                    1.dp,
                    if (uiState.estaEnLinea) NeonGreen.copy(alpha = 0.4f) else Color.Red.copy(alpha = 0.4f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (uiState.estaEnLinea) Icons.Default.Wifi else Icons.Default.WifiOff,
                        contentDescription = null,
                        tint = if (uiState.estaEnLinea) NeonGreen else Color(0xFFFF5252),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (uiState.estaEnLinea) "En línea: Sincronización activa" else "Sin conexión: Almacenando en búfer local",
                        color = if (uiState.estaEnLinea) NeonGreen else Color(0xFFFF5252),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Text(
                text = "Formulario de Registro en Terreno",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextLight,
                modifier = Modifier.padding(top = 4.dp)
            )

            ValidatedTextField(
                valor = uiState.nombre,
                alCambiarValor = { viewModel.onNombreCambiado(it) },
                etiqueta = "Nombre Completo *",
                iconoPrincipal = Icons.Default.Person,
                mensajeError = uiState.errorNombre
            )

            ValidatedTextField(
                valor = uiState.email,
                alCambiarValor = { viewModel.onEmailCambiado(it) },
                etiqueta = "Correo Electrónico *",
                iconoPrincipal = Icons.Default.Email,
                mensajeError = uiState.errorEmail,
                opcionesTeclado = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            ValidatedTextField(
                valor = uiState.telefono,
                alCambiarValor = { viewModel.onTelefonoCambiado(it) },
                etiqueta = "Teléfono de Contacto (9 dígitos) *",
                iconoPrincipal = Icons.Default.Phone,
                mensajeError = uiState.errorTelefono,
                opcionesTeclado = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )

            OutlinedTextField(
                value = uiState.cursoInteres,
                onValueChange = { viewModel.onCursoSeleccionado(it) },
                readOnly = true,
                label = { Text("Curso / Especialización", color = TextMuted) },
                leadingIcon = { Icon(Icons.Default.School, contentDescription = null, tint = DuocYellow) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = DuocCardSurface,
                    unfocusedContainerColor = DuocCardSurface,
                    unfocusedBorderColor = BorderSubtle,
                    focusedBorderColor = DuocYellow,
                    focusedTextColor = TextLight,
                    unfocusedTextColor = TextLight
                )
            )

            ValidatedTextField(
                valor = uiState.observaciones,
                alCambiarValor = { viewModel.onObservacionesCambiadas(it) },
                etiqueta = "Observaciones adicionales",
                iconoPrincipal = Icons.Default.Edit,
                mensajeError = null
            )

            val fotoActual = fotoLocal ?: uiState.fotoCapturada ?: fotoCapturada

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(
                    onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, BorderSubtle),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = DuocCardSurface,
                        contentColor = TextLight
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = null,
                        tint = DuocYellow,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Fotografiar Credencial", fontSize = 13.sp)
                }

                if (fotoActual != null) {
                    Image(
                        bitmap = fotoActual.asImageBitmap(),
                        contentDescription = "Miniatura credencial",
                        modifier = Modifier
                            .size(54.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .border(1.dp, DuocYellow, RoundedCornerShape(10.dp))
                    )
                }
            }

            val estaHabilitado = uiState.formularioValido && !uiState.estaGuardando

            Button(
                onClick = { viewModel.guardarProspecto() },
                enabled = estaHabilitado,
                contentPadding = PaddingValues(),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .padding(vertical = 4.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            if (estaHabilitado) {
                                Brush.horizontalGradient(listOf(Color(0xFF003882), DuocYellow))
                            } else {
                                Brush.horizontalGradient(listOf(Color(0xFF334155), Color(0xFF1E293B)))
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = null,
                            tint = if (estaHabilitado) TextLight else TextMuted
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (uiState.estaGuardando) "Guardando..." else "Guardar en Base de Datos Local",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = if (estaHabilitado) TextLight else TextMuted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}