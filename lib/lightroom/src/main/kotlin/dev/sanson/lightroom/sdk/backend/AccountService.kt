package dev.sanson.lightroom.sdk.backend

import dev.sanson.lightroom.sdk.backend.model.Account
import retrofit2.http.GET

internal interface AccountService {
    @GET("/v2/account")
    suspend fun getAccount(): Account
}
