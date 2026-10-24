package com.example.therionlabs_spa

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import com.example.therionlabs_spa.data.local.AppDatabase
import com.example.therionlabs_spa.data.repository.LeadRepository
import com.example.therionlabs_spa.ui.navigation.AppNavigation
import com.example.therionlabs_spa.ui.viewmodel.LeadFormViewModel
import com.example.therionlabs_spa.util.NetworkMonitor

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val baseDatos = AppDatabase.obtenerInstancia(applicationContext)
        val repositorio = LeadRepository(baseDatos.leadDao())
        val monitorRed = NetworkMonitor(applicationContext)
        val viewModel = LeadFormViewModel(repositorio, monitorRed)

        setContent {
            MaterialTheme {
                AppNavigation(viewModel = viewModel)
            }
        }
    }
}