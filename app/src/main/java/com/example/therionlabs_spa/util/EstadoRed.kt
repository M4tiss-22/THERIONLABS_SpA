package com.example.therionlabs_spa.util

import kotlinx.coroutines.flow.Flow

/**
 * Abstracción del estado de conectividad. El ViewModel depende de esta interfaz
 * (y no de NetworkMonitor, que necesita Context) para poder probarlo con pruebas unitarias.
 */
interface EstadoRed {
    val estaConectado: Flow<Boolean>
}
