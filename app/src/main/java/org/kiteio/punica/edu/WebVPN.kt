package org.kiteio.punica.edu

import com.fleeksoft.ksoup.Ksoup
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import io.ktor.http.Cookie
import io.ktor.http.HttpHeaders
import io.ktor.http.parameters
import io.ktor.utils.io.core.toByteArray
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.kiteio.punica.R
import org.kiteio.punica.candy.*
import org.kiteio.punica.edu.system.EduSystem
import org.kiteio.punica.getString
import org.kiteio.punica.request.Session
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * WebVPN
 */
object WebVPN : AgentAPI {
    override val root = "https://sec.gdufe.edu.cn"
    private const val COOKIE = "/rump_frontend/login/"  // Cookie
    private const val STATE = "/rump_frontend/getHomeParam/"  // 登录状态
    private const val SERVICE = "/rump_frontend/loginFromCas/"  // 服务器
    private val LOGIN = secondaryRoute(  // 登录
        "LjE5Ni4yMTYuMTY1LjE1My4xNjMuMTUxLjIxMy4xNjYuMTk4LjE2NC45NC4xNjAuMTUxLjIxOS4xNTUuMTUxLjE0Ny4xNTAuMTQ4LjIxNy4xMDAuMTU2LjE2MQ==/authserver/login"
    )


    override fun <T : ProxiedAPI> provide(api: T, route: T.() -> String) = route {
        when (api) {
            EduSystem.Companion -> secondaryRoute(
                "LjIwNS4yMTguMTY5LjE2NS45NC4xNTMuMTk5LjE2NS4xOTkuMTUxLjk0LjE1OC4xNTEuMjE5Ljk5LjE0OS4yMTE=${api.route()}?vpn-0"
            )

            SecondClass.Companion -> secondaryRoute(
                "LjE0OS4yMDYuMTUwLjE2NS4xNDUuMTYwLjIwMi45NC4yMDAuMTUwLjE2NS4xNTkuMTUyLjE0OC4xNTQuMTUwLjIxOC45NS4xNDcuMjEw${api.route()}?vpn-0-2ketang.gdufe.edu.cn"
            )

            else -> error("Unsupported consumer ${api::class.simpleName}")
        }
    }


    /**
     * 二级路由构建
     * @param route
     * @return [String]
     */
    private fun secondaryRoute(route: String) = "/webvpn/LjIwMy4yMTUuMTY1LjE2MQ==/${route}"


    /**
     * 登录
     * @param name 学号
     * @param pwd 门户密码
     * @param cookies Cookie
     * @return [WebVPN]
     */
    suspend fun login(
        name: String,
        pwd: String,
        cookies: MutableSet<Cookie>
    ) = withContext(Dispatchers.Default) {
        // 学校已经为 WebVPN 添加了短信登录
        errorOnToastLayer(getString(R.string.connect_to_campus_network_or_VPN))

        val session = Session(cookies).apply { fetch(route { COOKIE }) }

        val text = session.fetch(route { LOGIN }) {
            parameter("service", route { SERVICE })
        }.bodyAsText()

        val document = Ksoup.parse(text)
        // 已登录
        if (document.title() == "资源导航登录") return@withContext

        val execution = document.getElementById("execution")!!.attr("value")
        val key = document.getElementById("pwdEncryptSalt")!!.attr("value")

        // 登录提交
        val headers = session.post(
            route { LOGIN },
            parameters {
                append("username", name)
                append("password", aesEncode(pwd, key))
                append("_eventId", "submit")
                append("cllt", "userNameLogin")
                append("dllt", "generalLogin")
                append("execution", execution)
            }
        ).headers

        // 重定向获取新的 Cookie
        session.fetch(headers[HttpHeaders.Location]!!)

        // 检查登录状态
        session.fetch(route { STATE }).bodyAsText().json.run {
            if (getInt("code") != 0) errorOnToastLayer(getString("msg"))
        }
    }


    /**
     * AES 加密
     * @param text 密码
     * @param key
     * @return [String]
     */
    private fun aesEncode(text: String, key: String): String {
        fun randomString(length: Int): String {
            val sequence = "ABCDEFGHJKMNPQRSTWXYZabcdefhijkmnprstwxyz2345678"
            val random = SecureRandom()
            val stringBuilder = StringBuilder(length)
            for (i in 0 until length) {
                stringBuilder.append(sequence[random.nextInt(sequence.length)])
            }
            return stringBuilder.toString()
        }

        val plaintext = randomString(64) + text
        val iv = randomString(16)

        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        val secretKey = SecretKeySpec(key.toByteArray(), "AES")
        val ivSpec = IvParameterSpec(iv.toByteArray())
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec)
        val encryptedBytes = cipher.doFinal(plaintext.toByteArray())

        return Base64.getEncoder().encodeToString(encryptedBytes)
    }
}