package com.example.greenwichfirebaseauth.repository

import com.example.greenwichfirebaseauth.api.RetrofitInstance
import com.example.greenwichfirebaseauth.model.LoginRequest
import com.example.greenwichfirebaseauth.model.LoginResponse
import retrofit2.Response

class UserRepository {
    suspend fun loginUser(loginRequest: LoginRequest): Response<LoginResponse> {
        return RetrofitInstance.api.loginUser(loginRequest)
    }
}