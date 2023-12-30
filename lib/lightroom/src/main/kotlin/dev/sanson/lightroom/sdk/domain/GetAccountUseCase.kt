// Copyright (C) 2023, Jamie Sanson
// SPDX-License-Identifier: Apache-2.0
package dev.sanson.lightroom.sdk.domain

import dev.sanson.lightroom.sdk.backend.AccountService
import dev.sanson.lightroom.sdk.model.Account
import javax.inject.Inject

internal class GetAccountUseCase @Inject constructor(
    private val accountService: AccountService,
) {
    suspend operator fun invoke(): Account {
        return Account(accountService.getAccount().firstName)
    }
}
