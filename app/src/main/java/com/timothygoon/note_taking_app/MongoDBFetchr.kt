package com.timothygoon.note_taking_app

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.timothygoon.note_taking_app.api.LoginRequest
import com.timothygoon.note_taking_app.api.MongoDBApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

private const val TAG = "MongoDBFetchr"
class MongoDBFetchr {
    private val mongoDBApi: MongoDBApi
    init {
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://note-taking-app-server-t-goon.herokuapp.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
        mongoDBApi = retrofit.create(MongoDBApi::class.java)
    }

    fun checkUserCredentials(username: String, password: String): LiveData<String>{
        val responseLiveData: MutableLiveData<String> = MutableLiveData()
        val loginRequestBody: LoginRequest = LoginRequest(username, password)

        val loginRequest: Call<String> = mongoDBApi.checkUserCredentials(loginRequestBody)

        loginRequest.enqueue(object: Callback<String> {

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.e(TAG, "Failed to authenticate" )
            }
            override fun onResponse(call: Call<String>, response: Response<String>) {
                Log.d(TAG, "Response Received")
                val response : String? = response.body()

                responseLiveData.value = response
            }

        })

        return responseLiveData
    }

}