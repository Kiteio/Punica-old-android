package org.kiteio.punica.datastore

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import io.ktor.http.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * [MutableState]<[Boolean]> 序列器
 * @property descriptor
 */
class MutableStateBooleanSerializer: KSerializer<MutableState<Boolean>> {
    override val descriptor = PrimitiveSerialDescriptor("Cookie", PrimitiveKind.BOOLEAN)


    override fun deserialize(decoder: Decoder) = mutableStateOf(decoder.decodeBoolean())


    override fun serialize(encoder: Encoder, value: MutableState<Boolean>) {
        encoder.encodeBoolean(value.value)
    }
}


/**
 * Cookie 序列器
 * @property descriptor
 */
class CookieSerializer : KSerializer<Cookie> {
    override val descriptor = PrimitiveSerialDescriptor("Cookie", PrimitiveKind.STRING)


    override fun deserialize(decoder: Decoder) = decoder.decodeString()
        .split("=", limit = 2)
        .run { Cookie(get(0), get(1)) }


    override fun serialize(encoder: Encoder, value: Cookie) {
        encoder.encodeString("${value.name}=${value.value}")
    }
}