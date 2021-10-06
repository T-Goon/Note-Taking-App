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
        val loginRequestBody = LoginRequest(username, password)

        val loginRequest: Call<TokenResponse> = mongoDBApi.checkUserCredentials(loginRequestBody)

        loginRequest.enqueue(object: Callback<TokenResponse> {

            override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
                Log.e(TAG, "Failed to authenticate" )
            }
            override fun onResponse(call: Call<TokenResponse>, response: Response<TokenResponse>) {
                Log.d(TAG, "Response Received")
                val loginResponse : String? = response.body()?.token
                responseLiveData.value = loginResponse
            }

        })

        return responseLiveData
    }

    fun saveToServer(userTokenData: UserTokenData, notes: List<Note>): LiveData<String>{
        val responseLiveData: MutableLiveData<String> = MutableLiveData()

        val sendNotesRequestBody = SendNotesRequest(userTokenData.username, notes, userTokenData.token)

        val sendNotesRequest: Call<SaveResponse> = mongoDBApi.saveToServer(sendNotesRequestBody)

        sendNotesRequest.enqueue(object: Callback<SaveResponse>{
            override fun onResponse(call: Call<SaveResponse>, response: Response<SaveResponse>) {
                Log.d(TAG, "Data Saved!")
                val saveToServerResponse : String? = response.body()?.success.toString()
                responseLiveData.value = saveToServerResponse

            }

            override fun onFailure(call: Call<SaveResponse>, t: Throwable) {
                Log.e(TAG, "Failed to Save data")
            }

        }
        )

        return responseLiveData
    }

    fun loadFromServer(token: String): LiveData<List<LoadNote>>{
        val responseLiveData: MutableLiveData<List<LoadNote>> = MutableLiveData()

        val loadNotesRequestBody = LoadNotesRequest(token)

        val loadNotesRequest: Call<List<LoadNote>> = mongoDBApi.loadFromServer(loadNotesRequestBody)

        loadNotesRequest.enqueue(object: Callback<List<LoadNote>>{
            override fun onResponse(call: Call<List<LoadNote>>, response: Response<List<LoadNote>>) {
                Log.d(TAG, "Data Loaded!")
                val saveToServerResponse : List<LoadNote>? = response.body()
                responseLiveData.value = saveToServerResponse
            }
            override fun onFailure(call: Call<List<LoadNote>>, t: Throwable) {
                Log.e(TAG, "Failed to Load data")
            }

        }
        )

        return responseLiveData
    }

}