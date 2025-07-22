// Copyright (C) 2023, Jamie Sanson
// SPDX-License-Identifier: Apache-2.0
@file:OptIn(ExperimentalTime::class)

package dev.sanson.lightroom.sdk.backend.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.listSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeCollection
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Serializable
internal data class Asset(
    val id: String,
    val payload: Payload? = null,
)

/**
 * Note on deserialization in this file:
 * Kotlinx.serialization doesn't currently have any nicer means of deserializing
 * dynamically-named objects, such as what we see used for ratings and reviews. As such, the
 * best we can do for now is manually decode [JsonElement]s.
 */
@Serializable
internal data class Payload(
    @Contextual
    val captureDate: Instant,
    val xmp: Xmp,
    /**
     * "ratings": {
     *    "0a48ef0bacc30e3294f6b6daf1b517ac": {
     *      "rating": 5,
     *      "date": "2023-08-20T18:57:40.941Z"
     *    }
     * },
     */
    val ratings: JsonElement? = null,
    /**
     * "reviews": {
     *   "0a48ef0bacc30e3294f6b6daf1b517ac": {
     *     "date": "2023-08-20T18:57:40.337Z",
     *     "flag": "pick"
     *   }
     * }
     */
    val reviews: JsonElement? = null,
) {
    val rating: Int?
        get() {
            ratings ?: return null

            // The following is the UUID-named object
            val ratingObject =
                ratings.jsonObject.entries
                    .first()
                    .value.jsonObject

            return ratingObject["rating"]?.jsonPrimitive?.content?.toInt()
        }

    val picked: Boolean?
        get() {
            reviews ?: return null

            // The following is the UUID-named object
            val ratingObject =
                reviews.jsonObject.entries
                    .first()
                    .value.jsonObject

            return ratingObject["flag"]?.jsonPrimitive?.content?.let {
                when (it) {
                    "pick" -> true
                    "reject" -> false
                    else -> null
                }
            }
        }
}

@Serializable
internal data class Xmp(
    val tiff: Tiff,
    val exif: Exif,
    val aux: Aux,
    val dc: Dc? = null,
)

@Serializable
internal data class Tiff(
    @SerialName("Make")
    val make: String,
    @SerialName("Model")
    val model: String,
)

@Serializable
internal data class Exif(
    @SerialName("FNumber")
    val fNumber: Fraction,
    @SerialName("ExposureTime")
    val exposureTime: Fraction,
    @SerialName("FocalLength")
    val focalLength: Fraction,
    @SerialName("ISOSpeedRatings")
    val iso: Int,
)

@Serializable
internal data class Aux(
    @SerialName("Lens")
    val lens: String,
)

@Serializable
internal data class Dc(
    val subject: JsonElement? = null,
) {
    /**
     * "subject": {
     *     "bridge": true,
     *     "grungy": true
     *  }
     */
    val subjects: List<String>? get() = subject?.jsonObject?.entries?.map { it.key }
}

@Serializable(with = FractionSerializer::class)
internal data class Fraction(
    val numerator: Int,
    val denominator: Int,
)

@OptIn(ExperimentalSerializationApi::class)
internal object FractionSerializer : KSerializer<Fraction> {
    override val descriptor: SerialDescriptor =
        listSerialDescriptor<Int>()

    override fun deserialize(decoder: Decoder): Fraction =
        decoder.decodeStructure(descriptor) {
            Fraction(
                numerator = decodeIntElement(descriptor, index = decodeElementIndex(descriptor)),
                denominator = decodeIntElement(descriptor, index = decodeElementIndex(descriptor)),
            )
        }

    override fun serialize(
        encoder: Encoder,
        value: Fraction,
    ) {
        encoder.encodeCollection(descriptor, collectionSize = 2) {
            encodeIntElement(descriptor, index = 0, value.numerator)
            encodeIntElement(descriptor, index = 1, value.denominator)
        }
    }
}
