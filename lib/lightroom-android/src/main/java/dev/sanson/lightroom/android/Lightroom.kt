// Copyright (C) 2023, Jamie Sanson
// SPDX-License-Identifier: Apache-2.0
package dev.sanson.lightroom.android

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import dev.sanson.lightroom.android.auth.TokenRefreshWorker
import dev.sanson.lightroom.sdk.Lightroom

/**
 * Launch the Lightroom sign in flow
 *
 * For more details, see the [Authorize request documentation](https://developer.adobe.com/developer-console/docs/guides/authentication/UserAuthentication/IMS/#authorize-request)
 */
fun Lightroom.signIn(context: Context) {
    val intent = CustomTabsIntent.Builder().build()

    intent.launchUrl(context, Uri.parse(authManager.buildAuthUri()))
}

fun Lightroom.handleSignInResponse(intent: Intent) {
    val code = intent.data?.getQueryParameter("code") ?: error("\"code\" not found in redirect")
    authManager.onAuthorized(code)
}

/**
 * Install a WorkManager worker to periodically update our tokens, ensuring we
 * don't need to ask the user to sign in again
 *
 * @param context Application context
 */
fun Lightroom.Companion.installTokenRefresher(context: Context) {
    TokenRefreshWorker.enqueue(context)
}
