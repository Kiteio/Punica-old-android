package org.kiteio.punica.edu.system.api

import com.fleeksoft.ksoup.Ksoup
import io.ktor.client.statement.bodyAsText
import io.ktor.http.parameters
import org.kiteio.punica.edu.system.EduSystem

/**
 * 课程成绩
 * @receiver [EduSystem]
 * @return [SchoolReport]
 */
suspend fun EduSystem.schoolReport(): SchoolReport {
    val text = session.post(
        route { SCHOOL_REPORT },
        parameters {
            append("fxkc", "0")
            append("xsfs", "all")
        }
    ).bodyAsText()

    val document = Ksoup.parse(text)
    val table = document.getElementById("dataList")!!
    val rows = table.getElementsByTag("tr")

    // 评估
    val evaluation = try {
        document.getElementsByClass("Nsb_pw")[2].run {
            val textNodes = textNodes()
            val childTextNodes = children().textNodes()

            val stringBuilder = StringBuilder()
            for (index in 1..<childTextNodes.lastIndex) {
                stringBuilder.append(
                    textNodes[index + 4].text().trim(), childTextNodes[index]
                )
            }

            stringBuilder.toString()
        }
    } catch (_: Throwable) {
        ""
    }

    val items = arrayListOf<SchoolReportItem>()
    for (index in rows.size - 1 downTo 1) {
        val infos = rows[index].getElementsByTag("td")
        items.add(
            SchoolReportItem(
                semester = infos[1].text(),
                id = infos[2].text(),
                name = infos[3].text(),
                usualScore = infos[4].text(),
                experimentScore = infos[5].text(),
                examScore = infos[6].text(),
                score = infos[7].text(),
                point = infos[8].text(),
                classHours = infos[9].text(),
                examMode = infos[10].text(),
                type = infos[11].text(),
                sort = infos[12].text(),
                examSort = infos[14].text()
            )
        )
    }

    return SchoolReport(name, evaluation, items)
}


/**
 * 课程成绩
 * @property username 学号
 * @property evaluation 评估
 * @property items
 */
class SchoolReport(
    val username: String,
    val evaluation: String,
    val items: List<SchoolReportItem>
)


/**
 * 课程成绩项
 * @property semester 学年
 * @property id 课程编号
 * @property name 课程名
 * @property usualScore 平时成绩
 * @property experimentScore 实验成绩
 * @property examScore 考试成绩
 * @property score 总成绩
 * @property point 学分
 * @property classHours 总学时
 * @property examMode 考核方式
 * @property type 课程属性
 * @property sort 课程性质
 * @property examSort 考试性质
 */
data class SchoolReportItem(
    val semester: String,
    val id: String,
    val name: String,
    val usualScore: String,
    val experimentScore: String,
    val examScore: String,
    val score: String,
    val point: String,
    val classHours: String,
    val examMode: String,
    val type: String,
    val sort: String,
    val examSort: String
)