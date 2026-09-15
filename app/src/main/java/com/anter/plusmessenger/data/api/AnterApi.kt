package com.anter.plusmessenger.data.api

import com.anter.plusmessenger.data.api.models.ConversationsResponse
import com.anter.plusmessenger.data.api.models.LoginRequest
import com.anter.plusmessenger.data.api.models.LoginResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AnterApi {

    @POST("api/mobile/auth/login")
    suspend fun login(@Body body: LoginRequest): LoginResponse

    // التوكن يُضاف تلقائيًا من AuthInterceptor
    @GET("api/mobile/conversations")
    suspend fun getConversations(): ConversationsResponse
}
