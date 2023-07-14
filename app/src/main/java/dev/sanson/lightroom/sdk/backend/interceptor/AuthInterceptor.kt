package dev.sanson.lightroom.sdk.backend.interceptor

import dev.sanson.lightroom.sdk.backend.auth.CredentialStore
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val credentialStore: CredentialStore,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val credential = runBlocking { credentialStore.credential.firstOrNull() }

        val request = if (credential == null) {
            chain.request()
        } else {
            chain.request().newBuilder()
                .addHeader("Authorization", "Bearer ${credential.accessToken}")
                .build()
        }

        return chain.proceed(request)
    }
}
