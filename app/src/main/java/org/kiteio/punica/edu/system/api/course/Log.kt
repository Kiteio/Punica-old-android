package org.kiteio.punica.edu.system.api.course

import com.fleeksoft.ksoup.Ksoup
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.kiteio.punica.edu.system.CourseSystem
import org.kiteio.punica.edu.system.CourseSystem.Companion.fixTeacherName

/**
 * 退课日志
 * @receiver [CourseSystem]
 * @return [List]<[CourseLog]>
 */
suspend fun CourseSystem.log() = withContext(Dispatchers.Default) {
    val document = Ksoup.parse(session.fetch(route { LOG }).bodyAsText())
    val table = document.getElementsByTag("tbody")[0]
    val rows = table.getElementsByTag("tr")

    val logs = arrayListOf<CourseLog>()
    for (row in rows) {
        row.getElementsByTag("td").also {
            logs.add(
                CourseLog(
                    courseId = it[0].text(),
                    courseName = it[1].text(),
                    point = it[2].text(),
                    courseType = it[3].text(),
                    teacher = it[4].text().fixTeacherName(),
                    classTime = it[5].run {
                        if(text().isBlank()) emptyList()
                        else textNodes().map { textNode -> textNode.text() }
                    },
                    courseSort = it[6].text(),
                    operation = it[7].text(),
                    time = it[8].text(),
                    operator = it[9].text(),
                    desc = it[10].text()
                )
            )
        }
    }
    return@withContext logs
}


/**
 * 退课日志
 * @property courseId 课程编号
 * @property courseName 课程名
 * @property point 学分
 * @property courseType 课程属性
 * @property teacher 教师
 * @property classTime 上课时间
 * @property courseSort 选课分类
 * @property operation 退课类型
 * @property time 操作时间
 * @property operator 操作者
 * @property desc 操作说明
 */
data class CourseLog(
    val courseId: String,
    val courseName: String,
    val point: String,
    val courseType: String,
    val teacher: String,
    val classTime: List<String>,
    val courseSort: String,
    val operation: String,
    val time: String,
    val operator: String,
    val desc: String
)