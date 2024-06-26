package org.kiteio.punica.edu.system.api

import com.fleeksoft.ksoup.Ksoup
import io.ktor.client.statement.bodyAsText
import org.kiteio.punica.edu.system.EduSystem

/**
 * 校园网默认密码
 * @receiver [EduSystem]
 * @return [String]
 */
suspend fun EduSystem.campusNetPwd(): String {
    val text = session.fetch(route { CARD }).bodyAsText()

    val document = Ksoup.parse(text)
    val table = document.getElementById("xjkpTable")!!

    return table.getElementsByTag("tr")[43].getElementsByTag("td")[3]
        .text().substring(10)
}