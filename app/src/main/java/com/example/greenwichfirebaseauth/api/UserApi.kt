package com.example.greenwichfirebaseauth.api

import com.example.greenwichfirebaseauth.model.LoginRequest
import com.example.greenwichfirebaseauth.model.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface UserApi {

    @POST("/client-register")
    suspend fun loginUser(@Body loginRequest: LoginRequest): Response<LoginResponse>
}