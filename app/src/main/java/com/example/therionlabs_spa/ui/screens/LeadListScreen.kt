package com.example.therionlabs_spa.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.therionlabs_spa.ui.viewmodel.LeadFormViewModel

@Optin(ExperimentalMaterial3Api::class)
@Composable
fun LeadListScreen(
    viewModel: LeadFormViewModel,
    alRegresar: () -> Unit
) {
    val prospectos by viewModel.listaProspectos.collectAsState()
    val totalPendientes by viewModel.totalPendientes.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registros Locales ($totalPendientes pend.)") },
                navigationIcon = {
                    IconButton(onClick = alRegresar) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar")
                    }
                }
            )
        }
    ) { relleno ->
        if (prospectos.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(relleno),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No hay prospectos guardados en la base local",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(relleno)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(prospectos) { prospecto ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = prospecto.nombre,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (prospecto.estaSincronizado)
                                            Icons.Default.CloudDone else Icons.Default.CloudOff,
                                        contentDescription = null,
                                        tint = if (prospecto.estaSincronizado)
                                            MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (prospecto.estaSincronizado) "Sincronizado" else "Pendiente",
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Correo: ${prospecto.email}",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                text = "Teléfono: ${prospecto.telefono}",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                text = "Curso: ${prospecto.cursoInteres}",
                                style = MaterialTheme.typography.bodySmall
                            )
                            if (prospecto.observaciones.isNotBlank()) {
                                Text(
                                    text = "Obs: ${prospecto.observaciones}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

