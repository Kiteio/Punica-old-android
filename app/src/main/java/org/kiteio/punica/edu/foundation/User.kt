package org.kiteio.punica.edu.foundation

import io.ktor.http.Cookie
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.kiteio.punica.datastore.Identified

/**
 * 用户
 * @property name 学号
 * @property pwd 门户密码
 * @property secondClassPwd 第二课堂密码
 * @property campusNetPwd 校园网密码
 * @property cookies Cookie
 * @property id [name]
 */
@Serializable
class User(
    val name: String,
    var pwd: String = "",
    var secondClassPwd: String = name,
    var campusNetPwd: String = "",
    val cookies: MutableSet<@Serializable(with = CookieSerializer::class) Cookie> = mutableSetOf(),
) : Identified() {
    override val id = name
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


/**
 * 校园网用户
 * @property name 学号
 * @property ip ip
 * @property desc 描述
 * @property id [name]
 */
@Serializable
class CampusNetUser(
    val name: String,
    val ip: String,
    val desc: String,
    override val id: String
) : Identified()