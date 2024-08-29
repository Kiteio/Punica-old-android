package org.kiteio.punica.request

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.cookie
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.HttpResponse
import io.ktor.http.Cookie
import io.ktor.http.HttpHeaders
import io.ktor.http.Parameters

private val httpClient = HttpClient(OkHttp)


/**
 * Ktor GET
 * @param url
 * @param block
 * @return [HttpResponse]
 */
suspend fun fetch(url: String, block: HttpRequestBuilder.() -> Unit = {}) =
    httpClient.get(url) { header(HttpHeaders.AcceptEncoding, "br"); block() }


/**
 * Ktor POST([submitForm])
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
    header(HttpHeaders.AcceptEncoding, "br")
    block()
}


/**
 * 设置 Cookie
 * @receiver [HttpRequestBuilder]
 * @param cookie
 */
fun HttpRequestBuilder.cookie(cookie: Cookie) = with(cookie) {
    cookie(name, value, maxAge ?: 0, expires, domain, path, secure, httpOnly, extensions)
}