package org.kiteio.punica.edu

import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.kiteio.punica.candy.*
import org.kiteio.punica.request.fetch
import java.net.Inet4Address
import java.net.NetworkInterface

/**
 * 校园网
 */
object CampusNet : API {
    override val root = "http://100.64.13.17"
    private const val STATUS = "/drcom/chkstatus"  // 状态
    private const val LOGIN = ":801/eportal/portal/login"  // 登录
    private const val LOGOUT = ":801/eportal/portal/logout"  // 退出

    private const val CALLBACK_NAME = "Punica"


    /**
     * 返回本机 ip，未连接网络则返回 null
     * @return [String]?
     */
    fun ip(): String? {
        NetworkInterface.getNetworkInterfaces().toList().forEach {
            val inetAddresses = it.inetAddresses
            for (inetAddress in inetAddresses.toList()) {
                if (!inetAddress.isLoopbackAddress && inetAddress is Inet4Address) {
                    return inetAddress.hostAddress
                }
            }
        }

        return null
    }


    /**
     * 登录
     * @param name 学号
     * @param pwd 校园网密码
     * @param ip ip
     */
    suspend fun login(name: String, pwd: String, ip: String) = withContext(Dispatchers.Default) {
        val json = fetch(route { LOGIN }) {
            parameter("callback", CALLBACK_NAME)
            parameter("user_account", ",0,$name")
            parameter("user_password", pwd)
            parameter("wlan_user_ip", ip)
        }.jsonString().json

        if (json.getInt("result") != 1) {
            catch({ errorOnToastLayer(json.getString("msg")) }) {
                if (json.getInt("ret_code") == 2 || ip == status().getString("v4ip"))
                    return@withContext
                else json.getString("msg")
            }
        }
    }


    /**
     * 状态
     * @return [JSONObject]
     */
    private suspend fun status() =
        fetch(route { STATUS }) { parameter("callback", CALLBACK_NAME) }.jsonString().json


    /**
     * 处理 [HttpResponse.bodyAsText] 为 json 字符串
     * @receiver [HttpResponse]
     * @return [String]
     */
    private suspend fun HttpResponse.jsonString() = withContext(Dispatchers.Default) {
        bodyAsText().trim().run { substring(CALLBACK_NAME.length + 1, length - 1) }
    }


    /**
     * 退出登录
     * @param ip
     */
    suspend fun logout(ip: String) {
        fetch(route { LOGOUT }) {
            parameter("callback", CALLBACK_NAME)
            parameter("wlan_user_ip", ip)
        }
    }
}