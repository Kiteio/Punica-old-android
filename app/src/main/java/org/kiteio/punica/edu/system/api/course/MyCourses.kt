package org.kiteio.punica.edu.system.api.course

import com.fleeksoft.ksoup.Ksoup
import io.ktor.client.statement.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.kiteio.punica.edu.system.CourseSystem
import org.kiteio.punica.edu.system.CourseSystem.Companion.fixTeacherName

/**
 * 选课课表
 * @receiver [CourseSystem]
 * @return [List]<[MyCourse]>
 */
suspend fun CourseSystem.myCourses(): List<MyCourse> = withContext(Dispatchers.Default) {
    val document = Ksoup.parse(session.fetch(route { MY_COURSE }).bodyAsText())
    val table = document.getElementsByTag("tbody")[0]
    val rows = table.getElementsByTag("tr")

    val myCourses = arrayListOf<MyCourse>()
    for (row in rows) {
        val items = row.getElementsByTag("td")

        myCourses.add(
            MyCourse(
                id =  items[0].text(),
                operateId = items[8].child(0).attr("href").substring(21, 36),
                name = items[1].text(),
                point =  items[2].text(),
                type =  items[3].text(),
                teacher = items[4].text().fixTeacherName(),
                time = items[5].wholeText(),
                area = items[6].wholeText()
            )
        )
    }
    return@withContext myCourses
}


/**
 * 已选课程
 * @property id 课程编号
 * @property operateId
 * @property name 课程名
 * @property point 学分
 * @property type 课程属性
 * @property teacher 教师
 * @property time 上课时间
 * @property area 上课地点
 */
data class MyCourse(
    val id: String,
    val operateId: String,
    val name: String,
    val point: String,
    val type: String,
    val teacher: String,
    val time: String,
    val area: String
)