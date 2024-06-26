package org.kiteio.punica.edu.system.api.course

import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import io.ktor.http.Parameters
import io.ktor.http.encodeURLParameter
import io.ktor.http.parameters
import org.json.JSONObject
import org.kiteio.punica.candy.json
import org.kiteio.punica.edu.foundation.Campus
import org.kiteio.punica.edu.system.CourseSystem
import org.kiteio.punica.edu.system.CourseSystem.Companion.fixTeacherName
import java.time.DayOfWeek

/**
 * 课程列表
 * @receiver [CourseSystem]
 * @param sort
 * @param pageIndex
 * @param count
 * @return [List]<[Course]>
 */
suspend fun CourseSystem.list(sort: Unsearchable, pageIndex: Int = 0, count: Int = 15) = parse(
    session.post(
        route { courseListRoute(sort) },
        form(pageIndex, count)
    ).bodyAsText().json,
    sort
)


/**
 * 课程搜索
 * @receiver [CourseSystem]
 * @param sort
 * @param name 课程名
 * @param teacher 教师
 * @param dayOfWeek 星期几
 * @param section 节次
 * @param emptyOnly 过滤已满
 * @param filterConflicting 过滤冲突课程
 * @param campus 校区
 * @param pageIndex
 * @param count
 * @return [List]<[Course]>
 */
suspend fun CourseSystem.search(
    sort: Searchable,
    name: String = "",
    teacher: String = "",
    dayOfWeek: DayOfWeek? = null,
    section: Section? = null,
    emptyOnly: Boolean = false,
    filterConflicting: Boolean = false,
    campus: Campus? = null,
    pageIndex: Int = 0,
    count: Int = 15
) = parse(
    session.post(
        route { courseListRoute(sort) },
        form(pageIndex, count)
    ) {
        parameter("kcxx", name.encodeURLParameter())
        parameter("skls", teacher.encodeURLParameter())
        parameter("skxq", dayOfWeek?.value ?: "")
        parameter("skjc", section?.value ?: "")
        parameter("sfym", emptyOnly)
        parameter("sfct", filterConflicting)
        if (sort is Sort.General) {
            parameter("xq", campus?.id ?: "")  // 校区
        }
    }.bodyAsText().json,
    sort
)


/**
 * 节次
 * @property value
 */
sealed class Section(pair: Pair<Int, Int>) {
    val value = "${pair.first}-${pair.second}"

    data object First : Section(1 to 2)
    data object Second : Section(3 to 4)
    data object Third : Section(5 to 6)
    data object Fourth : Section(7 to 8)
    data object Fifth : Section(9 to 10)
    data object Sixth : Section(11 to 12)
}


/**
 * 表单
 * @param pageIndex 页标
 * @param count 单页数量
 * @return [Parameters]
 */
private fun form(pageIndex: Int, count: Int) = parameters {
    append("iDisplayStart", (count * pageIndex).toString())
    append("iDisplayLength", count.toString())
}


/**
 * 将 [json] 解析为 [Course] 列表
 * @param json
 * @return [List]<[Course]>
 */
private fun parse(json: JSONObject, sort: Sort): List<Course> {
    val rawCourses = json.getJSONArray("aaData")

    val courses = arrayListOf<Course>()
    for (index in 0..<rawCourses.length()) {
        rawCourses.getJSONObject(index).apply {
            courses.add(
                Course(
                    operateId = getString("jx0404id"),
                    id = getString("kch"),
                    name = getString("kcmc"),
                    teacher = getString("skls").fixTeacherName(),
                    point = getString("xf"),
                    campus = if (getInt("xqid") == 1) Campus.Guangzhou else Campus.Foshan,
                    time = getString("sksj"),
                    area = getString("skdd"),
                    total = getString("xxrs"),
                    remaining = getString("syrs"),
                    status = getString("ctsm"),
                    department = getString("dwmc"),
                    classHours = getString("zxs"),
                    examMode = try {
                        getString("khfs")
                    } catch (e: Throwable) {
                        null
                    },
                    selectable = getInt("sfkfxk") == 1,
                    selected = getInt("sfYx") == 1,
                    sort = sort
                )
            )
        }
    }

    return courses
}

/**
 * 课程
 * @property operateId 操作 id
 * @property id 课程编号
 * @property name 课程名
 * @property teacher 教师
 * @property point 学分
 * @property campus 校区
 * @property time 上课时间
 * @property area 上课地点
 * @property total 总数量
 * @property remaining 剩余数量
 * @property status 冲突情况
 * @property department 开课部门
 * @property classHours 总学时
 * @property examMode 考核方式
 * @property selectable 是否开放选课
 * @property selected 是否已选
 */
data class Course(
    val operateId: String,
    val id: String,
    val name: String,
    val teacher: String,
    val point: String,
    val campus: Campus,
    val time: String,
    val area: String,
    val total: String,
    val remaining: String,
    val status: String,
    val department: String,
    val classHours: String,
    val examMode: String?,
    val selectable: Boolean,
    val selected: Boolean,
    val sort: Sort
)