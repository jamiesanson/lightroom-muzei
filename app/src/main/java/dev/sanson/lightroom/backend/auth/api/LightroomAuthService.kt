package dev.sanson.lightroom.backend.auth.api

import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface LightroomAuthService {

    @POST("/ims/token/v3")
    suspend fun fetchToken(
        @Body body: RequestBody,
        @Query("client_id") clientId: String,
        @Header("Content-Type") contentType: String = "application/x-www-form-urlencoded"
    ): TokenResponse
}