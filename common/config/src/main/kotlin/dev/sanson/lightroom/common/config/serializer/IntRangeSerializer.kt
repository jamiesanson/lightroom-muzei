package dev.sanson.lightroom.common.config.serializer

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure

/**
 * Serializer for [IntRange]
 */
internal object IntRangeSerializer : KSerializer<IntRange> {
    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor("kotlin.ranges.IntRange") {
            element<Int>("start")
            element<Int>("endInclusive")
        }

    @OptIn(ExperimentalSerializationApi::class)
    override fun deserialize(decoder: Decoder): IntRange =
        decoder.decodeStructure(descriptor) {
            val start =
                decodeIntElement(
                    descriptor = descriptor,
                    index = descriptor.getElementIndex("start"),
                )
            val endInclusive =
                decodeIntElement(
                    descriptor = descriptor,
                    index = descriptor.getElementIndex("endInclusive"),
                )

            IntRange(start, endInclusive)
        }

    override fun serialize(
        encoder: Encoder,
        value: IntRange,
    ) {
        encoder.encodeStructure(descriptor) {
            encodeIntElement(descriptor, index = 0, value = value.first)
            encodeIntElement(descriptor, index = 1, value = value.last)
        }
    }
}
