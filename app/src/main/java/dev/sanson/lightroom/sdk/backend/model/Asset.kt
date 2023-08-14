package dev.sanson.lightroom.sdk.backend.model

import kotlinx.datetime.LocalDateTime
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
    val captureDate: LocalDateTime,
    val xmp: Xmp,
)

@Serializable
data class Xmp(
    val tiff: Tiff,
    val exif: Exif,
    val aux: Aux,
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
