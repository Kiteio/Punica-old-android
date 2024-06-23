package org.kiteio.punica.edu.system.api

import com.fleeksoft.ksoup.Ksoup
import io.ktor.client.statement.bodyAsText
import org.kiteio.punica.candy.route
import org.kiteio.punica.edu.system.EduSystem

/**
 * 等级成绩
 * @receiver [EduSystem]
 * @return [LevelReport]
 */
suspend fun EduSystem.levelReport(): LevelReport {
    val text = session.fetch(EduSystem.route { LEVEL_REPORT }).bodyAsText()

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

    return LevelReport(name, items)
}


/**
 * 等级成绩
 * @property username 学号
 * @property items
 */
class LevelReport(val username: String, val items: List<LevelReportItem>)


/**
 * 等级成绩项
 * @property name 科目
 * @property time 时间
 * @property score 成绩
 */
data class LevelReportItem(
    val name: String,
    val time: String,
    val score: String
)