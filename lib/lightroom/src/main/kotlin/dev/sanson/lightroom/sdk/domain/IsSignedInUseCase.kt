// Copyright (C) 2023, Jamie Sanson
// SPDX-License-Identifier: Apache-2.0
package dev.sanson.lightroom.sdk.domain

import dev.sanson.lightroom.sdk.backend.auth.AuthManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import retrofit2.HttpException
import javax.inject.Inject

internal class IsSignedInUseCase @Inject constructor(
    private val scope: CoroutineScope,
    private val authManager: AuthManager,
    private val retrieveAccount: GetAccountUseCase,
) {
    operator fun invoke(): SharedFlow<Boolean> =
        authManager.hasCredentials
            .map { hasCredential ->
                hasCredential &&
                    runCatching { retrieveAccount() }.fold(
                        onSuccess = { true },
                        onFailure = { error -> error !is HttpException },
                    )
            }.shareIn(scope, SharingStarted.Eagerly, replay = 1)
}
