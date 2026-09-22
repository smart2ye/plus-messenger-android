package com.anter.plusmessenger.data.api

import com.anter.plusmessenger.data.api.models.ConversationsResponse
import com.anter.plusmessenger.data.api.models.LoginRequest
import com.anter.plusmessenger.data.api.models.LoginResponse
import com.anter.plusmessenger.data.api.models.MessagesResponse
import com.anter.plusmessenger.data.api.models.SendMessageRequest
import com.anter.plusmessenger.data.api.models.SendMessageResponse
import com.anter.plusmessenger.data.api.models.TypingRequest
import com.anter.plusmessenger.data.api.models.TypingResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface AnterApi {

    @POST("api/mobile/auth/login")
    suspend fun login(@Body body: LoginRequest): LoginResponse

    @GET("api/mobile/conversations")
    suspend fun getConversations(): ConversationsResponse

    @GET("api/mobile/conversations/{username}/messages")
    suspend fun getMessages(
        @Path("username") username: String,
        @Query("afterId") afterId: Int = 0
    ): MessagesResponse

    @POST("api/mobile/conversations/{username}/messages")
    suspend fun sendMessage(
        @Path("username") username: String,
        @Body body: SendMessageRequest
    ): SendMessageResponse

    @POST("api/mobile/conversations/{username}/typing")
    suspend fun sendTyping(
        @Path("username") username: String,
        @Body body: TypingRequest
    ): TypingResponse

    @GET("api/mobile/conversations/{username}/activity")
    suspend fun getActivity(
        @Path("username") username: String
    ): TypingResponse
}
