package dev.sanson.lightroom.core.data

import androidx.datastore.core.Serializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import java.io.InputStream
import java.io.OutputStream

/**
 * Convenience function for creating a [JsonSerializer] without having to manually
 * specify serializer
 */
@Suppress("ktlint:standard:function-naming")
inline fun <reified T : Any> JsonSerializer(): JsonSerializer<T> {
    return JsonSerializer(serializer = serializer<T>())
}

/**
 * DataStore Serializer allowing for serialization of any serializable class [T]
 *
 * @param serializer [KSerializer] to use when saving and restoring Json text
 */
class JsonSerializer<T : Any>(
    private val serializer: KSerializer<T>,
) : Serializer<T?> {
    override val defaultValue: T? = null

    override suspend fun readFrom(input: InputStream): T? {
        return runCatching {
            val serialText = input.bufferedReader().use { it.readText() }

            Json.decodeFromString(serializer, serialText)
        }.getOrNull()
    }

    override suspend fun writeTo(
        t: T?,
        output: OutputStream,
    ) {
        if (t != null) {
            runCatching {
                output.bufferedWriter().use {
                    val serialText = Json.encodeToString(serializer, t)

                    it.write(serialText)
                }
            }
        }
    }
}
