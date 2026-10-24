package com.example.therionlabs_spa.util

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

object CameraManager {
    fun tienePermisoCamara(contexto: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            contexto,
            android.Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }
}