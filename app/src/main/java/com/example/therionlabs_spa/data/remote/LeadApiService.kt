package com.example.therionlabs_spa.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface LeadApiService {

    /** Envía un prospecto capturado en terreno al backend. Responde 2xx si quedó registrado. */
    @POST("api/leads")
    suspend fun enviarProspecto(@Body prospecto: LeadDto): Response<Unit>
}
