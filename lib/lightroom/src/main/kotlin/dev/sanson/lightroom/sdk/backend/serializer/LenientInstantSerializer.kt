// Copyright (C) 2023, Jamie Sanson
// SPDX-License-Identifier: Apache-2.0
package dev.sanson.lightroom.sdk.backend.serializer

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.UtcOffset
import kotlinx.datetime.toInstant
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * The Lightroom API doesn't include a timezone offset when said offset is in the "home" timezone,
 * i.e has an offset from current of 0 hours. This serializer performs a more lenient serialization,
 * attempting to use [Instant.parse], and falling back to [LocalDateTime.parse] + upcast
 * in the case of a failure
 */
internal object LenientInstantSerializer : KSerializer<Instant> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("Instant", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Instant {
        val rawDate = decoder.decodeString()

        return runCatching { Instant.parse(rawDate) }
            .getOrElse {
                LocalDateTime
                    .parse(rawDate)
                    .toInstant(UtcOffset.ZERO)
            }
    }

    override fun serialize(
        encoder: Encoder,
        value: Instant,
    ) {
        encoder.encodeString(value.toString())
    }
}
