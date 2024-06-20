package org.kiteio.punica.candy

/**
 * 网络 API
 * @property root
 */
interface API {
    val root: String
}


/**
 * 路由生成
 * @receiver [T]
 * @param block 返回二级路由
 * @return [String]
 */
inline fun <T: API> T.route(block: T.() -> String) = root + block()