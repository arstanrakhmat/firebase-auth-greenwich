package com.example.greenwichfirebaseauth.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.greenwichfirebaseauth.BaseResponse
import com.example.greenwichfirebaseauth.model.LoginRequest
import com.example.greenwichfirebaseauth.model.LoginResponse
import com.example.greenwichfirebaseauth.repository.UserRepository
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val userRepo = UserRepository()
    private val loginResult: MutableLiveData<BaseResponse<LoginResponse>> = MutableLiveData()

    fun loginUser(name: String, phoneNumber: String) {
        loginResult.value = BaseResponse.Loading()

        viewModelScope.launch {
            try {
                val loginRequest = LoginRequest(
                    first_name = name,
                    phone_number = phoneNumber
                )

                val response = userRepo.loginUser(loginRequest = loginRequest)

                if (response.code() == 200) {
                    loginResult.value = BaseResponse.Success(response.body())
                } else {
                    loginResult.value = BaseResponse.Error(response.message())
                }
            } catch (ex: java.lang.Exception) {
                loginResult.value = BaseResponse.Error(ex.message)
            }
        }
    }
}