package org.kiteio.punica.edu.foundation

import io.ktor.http.Cookie

/**
 * 用户
 * @property name 学号
 * @property pwd 门户密码
 * @property secondClassPwd 第二课堂密码
 * @property campusNetPwd 校园网密码
 * @property cookies Cookie
 * @constructor
 */
class User (
    val name: String,
    var pwd: String = "",
    var secondClassPwd: String = name,
    var campusNetPwd: String = "",
    val cookies: MutableSet<Cookie> = mutableSetOf(),
)