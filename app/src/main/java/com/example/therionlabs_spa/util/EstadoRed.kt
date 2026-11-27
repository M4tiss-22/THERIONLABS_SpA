package com.example.therionlabs_spa.util

import kotlinx.coroutines.flow.Flow

interface EstadoRed {
    val estaConectado: Flow<Boolean>
}
