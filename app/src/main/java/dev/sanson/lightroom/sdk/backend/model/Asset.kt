package dev.sanson.lightroom.sdk.backend.model

import kotlinx.datetime.Instant
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

@Serializable
data class Asset(
    val id: String,
    val payload: Payload? = null,
)

@Serializable
data class Payload(
    @Contextual
    val captureDate: Instant,
    val xmp: Xmp,
    /**
     * Ratings (stars) deserialization:
     * "ratings": {
     *    "0a48ef0bacc30e3294f6b6daf1b517ac": {
     *      "rating": 5,
     *      "date": "2023-08-20T18:57:40.941Z"
     *    }
     * },
     *
     * Reviews (pick, reject) deserialization:
     * "reviews": {
     *   "0a48ef0bacc30e3294f6b6daf1b517ac": {
     *     "date": "2023-08-20T18:57:40.337Z",
     *     "flag": "pick"
     *   }
     * }
     */
)

@Serializable
data class Xmp(
    val tiff: Tiff,
    val exif: Exif,
    val aux: Aux,
    /**
     * Keyword deserialization:
     * "dc": {
     *     "subject": {
     *         "bridge":true,
     *         "grungy":true
     *      }
     * }
     */
)

@Serializable
data class Tiff(
    @SerialName("Make")
    val make: String,
    @SerialName("Model")
    val model: String,
)

@Serializable
data class Exif(
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
data class Aux(
    @SerialName("Lens")
    val lens: String,
)

@Serializable(with = FractionSerializer::class)
data class Fraction(
    val numerator: Int,
    val denominator: Int,
)

@OptIn(ExperimentalSerializationApi::class)
object FractionSerializer : KSerializer<Fraction> {

    override val descriptor: SerialDescriptor =
        listSerialDescriptor<Int>()

    override fun deserialize(decoder: Decoder): Fraction =
        decoder.decodeStructure(descriptor) {
            Fraction(
                numerator = decodeIntElement(descriptor, index = decodeElementIndex(descriptor)),
                denominator = decodeIntElement(descriptor, index = decodeElementIndex(descriptor)),
            )
        }

    override fun serialize(encoder: Encoder, value: Fraction) {
        encoder.encodeCollection(descriptor, collectionSize = 2) {
            encodeIntElement(descriptor, index = 0, value.numerator)
            encodeIntElement(descriptor, index = 1, value.denominator)
        }
    }
}
