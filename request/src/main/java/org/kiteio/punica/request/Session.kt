package org.kiteio.punica.request

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.HttpResponse
import io.ktor.http.Cookie
import io.ktor.http.HttpHeaders
import io.ktor.http.Parameters
import io.ktor.http.setCookie

/**
 * 会话
 * @property httpClient
 * @property cookies Cookie 列表
 * @constructor
 * @param cookies 初始 Cookie 列表
 */
open class Session(private val cookies: MutableSet<Cookie> = mutableSetOf()) {
    private val httpClient = HttpClient(OkHttp)


    /**
     * Ktor GET，会自动保存、使用 Cookie
     * @param url
     * @param block
     * @return [HttpResponse]
     */
    suspend fun fetch(url: String, block: HttpRequestBuilder.() -> Unit = {}) =
        httpClient.get(url) {
            setCookies(cookies)
            header(HttpHeaders.AcceptEncoding, "br")
            block()
        }.apply { cookies.addAll(setCookie()) }


    /**
     * Ktor POST，会自动保存、使用 Cookie
     * @param url
     * @param formParameters
     * @param block
     * @return [HttpResponse]
     */
    suspend fun post(
        url: String,
        formParameters: Parameters = Parameters.Empty,
        block: HttpRequestBuilder.() -> Unit = {}
    ) = httpClient.submitForm(url, formParameters) {
        setCookies(cookies)
        header(HttpHeaders.AcceptEncoding, "br")
        block()
    }.apply { cookies.addAll(setCookie()) }


    /**
     * 设置 Cookie
     * @receiver [HttpRequestBuilder]
     * @param cookies
     */
    private fun HttpRequestBuilder.setCookies(cookies: Set<Cookie>) {
        cookies.forEach { cookie(it) }
    }
}