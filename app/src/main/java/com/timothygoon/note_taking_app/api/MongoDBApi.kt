package com.timothygoon.note_taking_app.api

import com.timothygoon.note_taking_app.database.TokenResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface MongoDBApi {

    @Headers("Content-Type: application/json")
    @POST("/login")
    fun checkUserCredentials(@Body loginRequest: LoginRequest): Call<TokenResponse>
}