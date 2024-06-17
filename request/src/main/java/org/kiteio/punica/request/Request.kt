package org.kiteio.punica.request

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.Parameters

private val httpClient = HttpClient(OkHttp)


/**
 * Ktor GET
 * @param url
 * @param block
 * @return [HttpResponse]
 */
suspend fun get(url: String, block: HttpRequestBuilder.() -> Unit = {}) =
    httpClient.get(url, block)


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
) = httpClient.submitForm(url, formParameters, block = block)