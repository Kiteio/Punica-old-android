package org.kiteio.punica.edu.system.api

import kotlinx.serialization.Serializable
import org.kiteio.punica.datastore.Identified
import org.kiteio.punica.edu.system.EduSystem

/**
 * 选课系统 [Token]
 * @receiver [EduSystem]
 * @param value
 * @return [Token]
 */
fun EduSystem.token(value: String) =
    Token(name, value)


/**
 * 选课系统 [Token]
 * @property username 学号
 * @property value Token 值
 * @property id [username]
 */
@Serializable
class Token(val username: String, val value: String): Identified() {
    override val id = username
}