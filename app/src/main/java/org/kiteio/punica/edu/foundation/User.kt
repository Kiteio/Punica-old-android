package org.kiteio.punica.edu.foundation

import io.ktor.http.Cookie
import kotlinx.serialization.Serializable
import org.kiteio.punica.datastore.CookieSerializer
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