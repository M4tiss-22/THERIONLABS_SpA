package com.example.therionlabs_spa.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

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
            label = { Text(etiqueta) },
            leadingIcon = { Icon(iconoPrincipal, contentDescription = null) },
            isError = mensajeError != null,
            trailingIcon = {
                if (mensajeError != null) {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = "Error en campo",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            },
            keyboardOptions = opcionesTeclado,
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        // Animación funcional de retroalimentación ante cambios de error
        AnimatedVisibility(
            visible = mensajeError != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Text(
                text = mensajeError ?: "",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 8.dp, top = 4.dp)
            )
        }
    }
}



