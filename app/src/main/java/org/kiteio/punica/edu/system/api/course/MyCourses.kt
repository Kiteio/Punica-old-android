package org.kiteio.punica.edu.system.api.course

import com.fleeksoft.ksoup.Ksoup
import io.ktor.client.statement.bodyAsText
import org.kiteio.punica.candy.route
import org.kiteio.punica.edu.system.CourseSystem
import org.kiteio.punica.edu.system.CourseSystem.Companion.fixTeacherName

/**
 * 选课课表
 * @receiver [CourseSystem]
 * @return [List]<[MyCourse]>
 */
suspend fun CourseSystem.myCourses(): List<MyCourse> {
    val document = Ksoup.parse(session.fetch(CourseSystem.route { MY_COURSE }).bodyAsText())
    val table = document.getElementsByTag("tbody")[0]
    val rows = table.getElementsByTag("tr")

    val myCourses = arrayListOf<MyCourse>()
    for (row in rows) {
        row.getElementsByTag("td").also {
            val timeTextNodes = it[5].textNodes()
            val areaTextNodes = it[6].textNodes()

            val classInfos = arrayListOf<ClassInfo>()
            for (index in timeTextNodes.indices) {
                val time = timeTextNodes[index].text()

                // 跳过没有上课时间的课程
                if (time.isBlank()) continue

                val timeInfos = time.split(" ")
                classInfos.add(
                    ClassInfo(
                        week = timeInfos[0],
                        dayOfWeek = timeInfos[1],
                        section = timeInfos[2],
                        area = areaTextNodes[index].text()
                    )
                )
            }

            myCourses.add(
                MyCourse(
                    id =  it[0].text(),
                    name = it[1].text(),
                    point =  it[2].text(),
                    type =  it[3].text(),
                    teacher = it[4].text().fixTeacherName(),
                    classInfos = classInfos
                )
            )
        }
    }
    return myCourses
}


/**
 * 已选课程
 * @property id 课程编号
 * @property name 课程名
 * @property point 学分
 * @property type 课程属性
 * @property teacher 教师
 * @property classInfos 上课信息
 */
data class MyCourse(
    val id: String,
    val name: String,
    val point: String,
    val type: String,
    val teacher: String,
    val classInfos: List<ClassInfo>
)


/**
 * 上课信息
 * @property week 周次
 * @property dayOfWeek 星期几
 * @property section 节次
 * @property area 地点
 */
data class ClassInfo(
    val week: String,
    val dayOfWeek: String,
    val section: String,
    val area: String
)