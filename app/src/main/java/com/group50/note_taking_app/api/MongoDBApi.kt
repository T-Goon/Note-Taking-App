package com.group50.note_taking_app.api

import com.group50.note_taking_app.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface MongoDBApi {

    @Headers("Content-Type: application/json")
    @POST("/login")
    fun checkUserCredentials(@Body loginRequest: LoginRequest): Call<TokenResponse>

    @Headers("Content-Type: application/json")
    @POST("/save")
    fun saveToServer(@Body sendNotesRequest: SendNotesRequest): Call<SaveResponse>

    @Headers("Content-Type: application/json")
    @POST("/load")
    fun loadFromServer(@Body loadNotesRequest: LoadNotesRequest): Call<List<LoadNote>>
}