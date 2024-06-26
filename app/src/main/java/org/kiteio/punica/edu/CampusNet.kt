package org.kiteio.punica.edu

import io.ktor.client.request.parameter
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import org.json.JSONObject
import org.kiteio.punica.candy.API
import org.kiteio.punica.candy.json
import org.kiteio.punica.candy.route
import org.kiteio.punica.request.fetch

/**
 * 校园网
 */
object CampusNet : API {
    override val root = "http://100.64.13.17"
    private const val STATUS = "/drcom/chkstatus"  // 状态
    private const val LOGIN = ":801/eportal/portal/login"  // 登录
    private const val LOGOUT = ":801/eportal/portal/logout"  // 退出

    private const val CALLBACK = "Punica"

    /**
     * 本机 ip
     * @return [String]
     */
    suspend fun ip(): String = status().getString("v46ip")


    /**
     * 登录
     * @param name 学号
     * @param pwd 校园网密码
     * @param ip ip
     */
    suspend fun login(name: String, pwd: String, ip: String) {
        val json = fetch(route { LOGIN }) {
            parameter("callback", CALLBACK)
            parameter("user_account", ",0,$name")
            parameter("user_password", pwd)
            parameter("wlan_user_ip", ip)
        }.jsonString().json

        if (json.getInt("result") != 1) {
            try {
                if (ip == status().getString("v4ip")) return
            } catch (_: Exception) {
                error(json.getString("msg"))
            }
        }
    }


    /**
     * 状态
     * @return [JSONObject]
     */
    private suspend fun status() = fetch(route { STATUS }) {
        parameter("callback", CALLBACK)
    }.jsonString().json


    /**
     * 处理 [HttpResponse.bodyAsText] 为 json 字符串
     * @receiver [HttpResponse]
     * @return [String]
     */
    private suspend fun HttpResponse.jsonString() =
        bodyAsText().trim().run { substring(CALLBACK.length + 1, length - 1) }


    /**
     * 退出登录
     * @param ip
     */
    suspend fun logout(ip: String) {
        fetch(route { LOGOUT }) {
            parameter("callback", CALLBACK)
            parameter("wlan_user_ip", ip)
        }
    }
}