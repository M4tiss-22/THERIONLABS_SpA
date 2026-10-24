package com.example.therionlabs_spa.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class NetworkMonitor(contexto: Context) {

    private val administradorConexion =
        contexto.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val estaConectado: Flow<Boolean> = callbackFlow {
        val respuestaRed = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(red: Network) {
                trySend(true)
            }

            override fun onLost(red: Network) {
                trySend(false)
            }
        }

        val peticionRed = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        administradorConexion.registerNetworkCallback(peticionRed, respuestaRed)

        val redActiva = administradorConexion.activeNetwork
        val capacidades = administradorConexion.getNetworkCapabilities(redActiva)
        val estadoInicial = capacidades?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
        trySend(estadoInicial)

        awaitClose {
            administradorConexion.unregisterNetworkCallback(respuestaRed)
        }
    }
}