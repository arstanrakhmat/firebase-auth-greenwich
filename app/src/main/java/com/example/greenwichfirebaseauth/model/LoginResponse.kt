package com.example.greenwichfirebaseauth.model

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("data")
    var data: User? = null
)

class User {
    var first_name: String? = null
    var phone_number: String? = null
    var token: String? = null
}
