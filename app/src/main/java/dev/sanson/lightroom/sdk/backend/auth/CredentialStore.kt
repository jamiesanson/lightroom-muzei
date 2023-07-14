package dev.sanson.lightroom.sdk.backend.auth

import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

@Serializable
data class Credential(
    val accessToken: String,
    val refreshToken: String,
) {

    companion object {

        val Serializer = object : Serializer<Credential?> {
            override val defaultValue: Credential? = null

            override suspend fun readFrom(input: InputStream): Credential? {
                return runCatching {
                    Json.decodeFromString<Credential>(input.bufferedReader().readText())
                }.getOrNull()
            }

            override suspend fun writeTo(t: Credential?, output: OutputStream) {
                if (t != null) {
                    runCatching {
                        val credentialString = Json.encodeToString(t)
                        output.bufferedWriter().write(credentialString)
                    }
                }
            }
        }
    }
}

interface CredentialStore {
    val credential: Flow<Credential?>

    suspend fun updateTokens(accessToken: String, refreshToken: String)
}

class DefaultCredentialStore(
    private val dataStore: DataStore<Credential?>,
) : CredentialStore {

    override val credential: Flow<Credential?> = dataStore.data

    override suspend fun updateTokens(accessToken: String, refreshToken: String) {
        dataStore.updateData {
            Credential(accessToken = accessToken, refreshToken = refreshToken)
        }
    }
}
