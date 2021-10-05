package com.timothygoon.note_taking_app.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface MongoDBApi {

    @Headers("Content-Type: application/json")
    @POST("/login")
    fun checkUserCredentials(@Body loginRequest: LoginRequest): Call<String>
}