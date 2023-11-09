package dev.sanson.lightroom.sdk.backend.auth.api

import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * The following service wraps the auth flows defined under User Authentication
 * in the Adobe creative cloud documentation.
 *
 * For more information, see the [service documentation](https://developer.adobe.com/developer-console/docs/guides/authentication/UserAuthentication/IMS)
 */
internal interface LightroomAuthService {

    @POST("/ims/token/v3")
    suspend fun fetchToken(
        @Body body: RequestBody,
        @Query("client_id") clientId: String,
        @Header("Content-Type") contentType: String = "application/x-www-form-urlencoded",
    ): TokenResponse
}
