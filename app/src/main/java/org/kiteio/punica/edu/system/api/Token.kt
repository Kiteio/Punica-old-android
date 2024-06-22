package org.kiteio.punica.edu.system.api

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
 * @property name 学号
 * @property value id
 * @constructor
 */
data class Token(val name: String, val value: String)