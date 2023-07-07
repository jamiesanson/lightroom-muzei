package dev.sanson.lightroom.backend.lightroom

import dev.sanson.lightroom.backend.lightroom.model.Account
import retrofit2.http.GET

interface AccountService {

    @GET("/v2/account")
    suspend fun getAccount(): Account
}