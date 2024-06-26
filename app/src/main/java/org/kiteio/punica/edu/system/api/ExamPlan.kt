package org.kiteio.punica.edu.system.api

import com.fleeksoft.ksoup.Ksoup
import io.ktor.client.statement.bodyAsText
import io.ktor.http.parameters
import org.kiteio.punica.edu.system.EduSystem
import org.kiteio.punica.edu.system.EduSystem.Companion.semester

/**
 * 考试安排
 * @receiver [EduSystem]
 * @return [ExamPlan]
 */
suspend fun EduSystem.examPlan(): ExamPlan {
    val text = session.post(
        route { EXAM_PLAN },
        parameters { append("xnxqid", semester) }
    ).bodyAsText()

    val document = Ksoup.parse(text)
    val table = document.getElementById("dataList")!!
    val rows = table.getElementsByTag("tr")

    val items = arrayListOf<ExamPlanItem>()
    for (index in 1..<rows.size) {
        val infos = rows[index].getElementsByTag("td")

        items.add(
            ExamPlanItem(
                id = infos[1].text(),
                name = infos[2].text(),
                time = infos[3].text().split("~").run { get(0) to get(1) },
                campus = infos[4].text(),
                area = infos[5].text()
            )
        )
    }

    return ExamPlan(name, items)
}


/**
 * 考试安排
 * @property username 学号
 * @property items
 */
class ExamPlan(
    val username: String,
    val items: List<ExamPlanItem>
)


/**
 * 考试安排项
 * @property id 课程编号
 * @property name 课程名
 * @property time 时间
 * @property campus 校区
 * @property area 地点
 */
data class ExamPlanItem(
    val id: String,
    val name: String,
    val time: Pair<String, String>,
    val campus: String,
    val area: String
)