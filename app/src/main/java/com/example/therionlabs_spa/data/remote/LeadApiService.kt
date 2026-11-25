package com.example.therionlabs_spa.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface LeadApiService {


    @POST("api/leads")
    suspend fun enviarProspecto(@Body prospecto: LeadDto): Response<Unit>
}
