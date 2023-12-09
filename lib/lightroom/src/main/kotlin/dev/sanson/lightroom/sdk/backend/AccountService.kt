// Copyright (C) 2023, Jamie Sanson
// SPDX-License-Identifier: Apache-2.0
package dev.sanson.lightroom.sdk.backend

import dev.sanson.lightroom.sdk.backend.model.Account
import retrofit2.http.GET

internal interface AccountService {
    @GET("/v2/account")
    suspend fun getAccount(): Account
}
