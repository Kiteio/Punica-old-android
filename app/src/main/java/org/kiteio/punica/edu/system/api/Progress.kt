package org.kiteio.punica.edu.system.api

import com.fleeksoft.ksoup.Ksoup
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import io.ktor.http.parameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.kiteio.punica.edu.system.EduSystem

/**
 * 学业进度
 * @receiver [EduSystem]
 * @return [Progress]
 */
suspend fun EduSystem.progress() = withContext(Dispatchers.Default) {
    val text = session.post(
        route { PROGRESS },
        parameters { append("xdlx", "0") }
    ) { parameter("type", "cx") }.bodyAsText()

    val document = Ksoup.parse(text)
    val tables = document.getElementsByTag("tbody")
    val progressTables = arrayListOf<ProgressTable>()

    for (index in 1..<tables.size) {
        val rows = tables[index].getElementsByTag("tr")
        val lastItems = rows.last()!!.children()

        val name = rows[0].child(0).textNodes()[0].text().trim()
        val requiredPoint = lastItems[1].text().trim()
        val gotPoint = lastItems[2].text().trim()

        val progressItems = arrayListOf<ProgressItem>()
        for (rowIndex in 2..<rows.lastIndex) {
            val elements = rows[rowIndex].children()

            progressItems.add(
                ProgressItem(
                    id = elements[2].text(),
                    name = elements[3].text(),
                    module = elements[0].text(),
                    point = elements[4].text(),
                    term = elements[5].text(),
                    privilege = elements[6].text().ifBlank { null },
                    gotPoint = elements[8].text()
                )
            )
        }

        progressTables.add(
            ProgressTable(
                name = name,
                requiredPoint = requiredPoint,
                gotPoint = gotPoint,
                items = progressItems
            )
        )
    }

    return@withContext Progress(name, progressTables)
}


/**
 * 学业进度
 * @property username 学号
 * @property tables
 */
class Progress(
    val username: String,
    val tables: List<ProgressTable>
)


/**
 * 学业进度表
 * @property name 表名
 * @property requiredPoint 要求学分
 * @property gotPoint 已获得学分
 * @property items
 */
data class ProgressTable(
    val name: String,
    val requiredPoint: String,
    val gotPoint: String,
    val items: List<ProgressItem>
)


/**
 * 学业进度项
 * @property id 课程编号
 * @property name 课程名
 * @property module 模块
 * @property point 学分
 * @property term 建议修读学期
 * @property privilege 免听、免修
 * @property gotPoint 已获得学分
 */
data class ProgressItem(
    val id: String,
    val name: String,
    val module: String,
    val point: String,
    val term: String,
    val privilege: String?,
    val gotPoint: String
)