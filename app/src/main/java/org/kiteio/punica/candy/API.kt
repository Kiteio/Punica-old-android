package org.kiteio.punica.candy

/**
 * 网络 API
 * @property root
 */
interface API {
    val root: String
}


/**
 * [API] 路由生成
 * @receiver [T]
 * @param block 返回二级路由
 * @return [String]
 */
inline fun <T : API> T.route(block: T.() -> String) = root + block()


/**
 * 代理 [API]
 */
interface AgentAPI : API {
    /**
     * 为 [api] 提供访问支持
     * @param api
     * @param route
     * @return [String]
     */
    fun <T : ProxiedAPI> provide(api: T, route: T.() -> String): String
}


/**
 * 可代理 [API]
 * @property agent
 */
interface ProxiedAPI : API {
    val agent: AgentAPI
}


/**
 * [ProxiedAPI] 路由生成
 * @receiver [T]
 * @param proxied 是否使用代理
 * @param block
 * @return [String]
 */
fun <T: ProxiedAPI> T.route(proxied: Boolean, block: T.() -> String) =
    if (proxied) agent.provide(this, block) else route(block)


/**
 * [ProxiedAPI] 所有者
 * @param T: [ProxiedAPI]
 * @property proxiedAPI
 * @property proxied 是否使用代理
 */
abstract class ProxiedAPIOwner<T: ProxiedAPI>(private val proxiedAPI: T) {
    abstract val proxied: Boolean


    /**
     * [ProxiedAPIOwner] 路由生成
     * @param block
     * @return [String]
     */
    fun route(block: T.() -> String) = proxiedAPI.route(proxied, block)
}