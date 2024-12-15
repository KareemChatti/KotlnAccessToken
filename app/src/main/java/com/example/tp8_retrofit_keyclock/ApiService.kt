package com.example.tp8_retrofit_keyclock

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface ApiService {
    @GET("customers")
    suspend fun getCustomers(
        @Header("Authorization") authToken: String
    ): Response<List<Customer>>
}
