package dev.sanson.lightroom.backend.auth

import kotlinx.coroutines.flow.Flow

interface Credential {
    val accessToken: String
    val refreshToken: String
}

interface CredentialStore {
    val credential: Flow<Credential?>

    fun updateTokens(accessToken: String, refreshToken: String)
}

class DefaultCredentialStore : CredentialStore {

    override val credential: Flow<Credential?>
        get() = TODO("Not yet implemented")

    override fun updateTokens(accessToken: String, refreshToken: String) {
        TODO("Not yet implemented; $accessToken; $refreshToken")
    }
}