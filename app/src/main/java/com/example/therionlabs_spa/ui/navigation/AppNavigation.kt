package com.example.therionlabs_spa.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.therionlabs_spa.ui.screens.LeadFormScreen
import com.example.therionlabs_spa.ui.screens.LeadListScreen
import com.example.therionlabs_spa.ui.viewmodel.LeadFormViewModel

sealed class Screen(val ruta: String) {
    data object Formulario : Screen("ruta_formulario")
    data object Listado : Screen("ruta_listado")
}

@Composable
fun AppNavigation(viewModel: LeadFormViewModel) {
    val controladorNavegacion = rememberNavController()

    NavHost(
        navController = controladorNavegacion,
        startDestination = Screen.Formulario.ruta
    ) {
        composable(Screen.Formulario.ruta) {
            LeadFormScreen(
                viewModel = viewModel,
                onNavigateToLista = {
                    controladorNavegacion.navigate(Screen.Listado.ruta)
                }
            )
        }
        composable(Screen.Listado.ruta) {
            LeadListScreen(
                viewModel = viewModel,
                alRegresar = {
                    controladorNavegacion.popBackStack()
                }
            )
        }
    }
}