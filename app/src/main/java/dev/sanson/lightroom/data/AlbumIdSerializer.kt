package dev.sanson.lightroom.data

import androidx.datastore.core.Serializer
import dev.sanson.lightroom.sdk.model.AlbumId
import java.io.InputStream
import java.io.OutputStream

object AlbumIdSerializer : Serializer<AlbumId?> {

    override val defaultValue: AlbumId? = null

    override suspend fun readFrom(input: InputStream): AlbumId {
        val id = input.readBytes().decodeToString()

        return AlbumId(id)
    }

    override suspend fun writeTo(t: AlbumId?, output: OutputStream) {
        t ?: return

        output.write(t.id.toByteArray())
    }
}
