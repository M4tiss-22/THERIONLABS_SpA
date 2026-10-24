package com.example.therionlabs_spa.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Colores Duoc Dark
private val DuocCardSurface = Color(0xFF1E293B)
private val DuocYellow = Color(0xFFFFAE00)
private val TextLight = Color(0xFFF8FAFC)
private val TextMuted = Color(0xFF94A3B8)
private val BorderSubtle = Color(0xFF334155)
private val ErrorRed = Color(0xFFFF5252)

@Composable
fun ValidatedTextField(
    valor: String,
    alCambiarValor: (String) -> Unit,
    etiqueta: String,
    iconoPrincipal: ImageVector,
    mensajeError: String?,
    opcionesTeclado: KeyboardOptions = KeyboardOptions.Default,
    modificador: Modifier = Modifier
) {
    Column(modifier = modificador.fillMaxWidth()) {
        OutlinedTextField(
            value = valor,
            onValueChange = alCambiarValor,
            label = {
                Text(
                    text = etiqueta,
                    color = if (mensajeError != null) ErrorRed else TextMuted
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = iconoPrincipal,
                    contentDescription = null,
                    tint = if (mensajeError != null) ErrorRed else DuocYellow
                )
            },
            isError = mensajeError != null,
            trailingIcon = {
                if (mensajeError != null) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Error en campo",
                        tint = ErrorRed
                    )
                }
            },
            keyboardOptions = opcionesTeclado,
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = DuocCardSurface,
                unfocusedContainerColor = DuocCardSurface,
                focusedBorderColor = DuocYellow,
                unfocusedBorderColor = BorderSubtle,
                errorBorderColor = ErrorRed,
                focusedTextColor = TextLight,
                unfocusedTextColor = TextLight,
                cursorColor = DuocYellow
            )
        )

        AnimatedVisibility(
            visible = mensajeError != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Text(
                text = mensajeError ?: "",
                color = ErrorRed,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 12.dp, top = 4.dp)
            )
        }
    }
}