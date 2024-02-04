// Copyright (C) 2024, Jamie Sanson
// SPDX-License-Identifier: Apache-2.0
package dev.sanson.lightroom.muzei.backend

import android.annotation.TargetApi
import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.Signature
import android.os.Build
import okhttp3.Interceptor
import okhttp3.Response
import java.security.MessageDigest
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

/**
 * Provide Android build-related headers to satisfy Google Cloud API key restrictions
 */
internal class AndroidClientInterceptor(
    private val applicationContext: Context,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request =
            chain.request().newBuilder()
                .addHeader("X-Android-Package", applicationContext.packageName)
                .addHeader("X-Android-Cert", applicationContext.applicationSHAOne)
                .build()

        return chain.proceed(request)
    }

    private val Context.applicationSHAOne: String get() {
        val signingInfo =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                SigningInfo.Api28
            } else {
                SigningInfo.Default
            }

        return signingInfo.getApplicationShaOne(this)
    }

    private sealed interface SigningInfo {
        fun getApplicationShaOne(context: Context): String

        @OptIn(ExperimentalEncodingApi::class)
        fun Signature.digestShaOne(): String {
            val digest =
                MessageDigest.getInstance("SHA-1")
                    .apply { update(this@digestShaOne.toByteArray()) }

            return Base64.encode(digest.digest())
        }

        data object Default : SigningInfo {
            @Suppress("DEPRECATION")
            override fun getApplicationShaOne(context: Context): String {
                val packageInfo =
                    context.packageManager
                        .getPackageInfo(context.packageName, PackageManager.GET_SIGNATURES)

                val signature = packageInfo.signatures.first()

                return signature.digestShaOne()
            }
        }

        @TargetApi(Build.VERSION_CODES.P)
        data object Api28 : SigningInfo {
            override fun getApplicationShaOne(context: Context): String {
                val packageInfo =
                    context.packageManager
                        .getPackageInfo(context.packageName, PackageManager.GET_SIGNING_CERTIFICATES)

                val certificateHistory = packageInfo.signingInfo.signingCertificateHistory
                val signature = certificateHistory.last()

                return signature.digestShaOne()
            }
        }
    }
}
