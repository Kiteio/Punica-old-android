package org.kiteio.punica.edu.system.api

import com.fleeksoft.ksoup.Ksoup
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.kiteio.punica.edu.system.EduSystem

/**
 * 校园网默认密码
 * @receiver [EduSystem]
 * @return [String]
 */
suspend fun EduSystem.campusNetPwd() = withContext(Dispatchers.Default) {
    val text = session.fetch(route { CARD }).bodyAsText()

    val document = Ksoup.parse(text)
    val table = document.getElementById("xjkpTable")!!

    return@withContext table.getElementsByTag("tr")[43].getElementsByTag("td")[3]
        .text().substring(10)
}