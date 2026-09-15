package com.anter.plusmessenger.data.api

import com.anter.plusmessenger.data.api.models.ConversationsResponse
import com.anter.plusmessenger.data.api.models.LoginRequest
import com.anter.plusmessenger.data.api.models.LoginResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface AnterApi {

    @POST("api/mobile/auth/login")
    suspend fun login(@Body body: LoginRequest): LoginResponse

    @GET("api/mobile/conversations")
    suspend fun getConversations(
        @Header("Authorization") bearer: String
    ): ConversationsResponse
}
