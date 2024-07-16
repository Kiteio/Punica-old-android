package org.kiteio.punica.edu.system.api

import com.fleeksoft.ksoup.Ksoup
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import org.kiteio.punica.datastore.Identified
import org.kiteio.punica.edu.system.EduSystem

/**
 * 等级成绩
 * @receiver [EduSystem]
 * @return [LevelReport]
 */
suspend fun EduSystem.levelReport() = withContext(Dispatchers.Default) {
    val text = session.fetch(route { LEVEL_REPORT }).bodyAsText()

    val document = Ksoup.parse(text)
    val table = document.getElementById("dataList")!!
    val rows = table.getElementsByTag("tr")

    val items = arrayListOf<LevelReportItem>()
    for (index in 2..<rows.size) {
        val infos = rows[index].children()
        items.add(
            LevelReportItem(
                name = infos[1].text(),
                time = infos[8].text(),
                score = infos[4].text()
            )
        )
    }

    return@withContext LevelReport(name, items)
}


/**
 * 等级成绩
 * @property username 学号
 * @property items
 * @property id [username]
 */
@Serializable
class LevelReport(val username: String, val items: List<LevelReportItem>) : Identified() {
    override val id = username
}


/**
 * 等级成绩项
 * @property name 科目
 * @property time 时间
 * @property score 成绩
 */
@Serializable
data class LevelReportItem(
    val name: String,
    val time: String,
    val score: String
)