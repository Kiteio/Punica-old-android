package org.kiteio.punica.edu.system.api.course

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import org.kiteio.punica.candy.ifNotBlank
import org.kiteio.punica.candy.json
import org.kiteio.punica.datastore.Identified
import org.kiteio.punica.datastore.MutableStateBooleanSerializer
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
suspend fun CourseSystem.list(
    sort: Sort.Unsearchable,
    pageIndex: Int = 0,
    count: Int = 15
) = withContext(Dispatchers.Default) {
    parse(token.username, session.post(route { courseListRoute(sort) }, form(pageIndex, count)), sort)
}


/**
 * 课程搜索
 * @receiver [CourseSystem]
 * @param sort
 * @param searchParams
 * @param pageIndex
 * @param count
 * @return [List]<[Course]>
 */
suspend fun CourseSystem.search(
    sort: Sort.Searchable,
    searchParams: SearchParams,
    pageIndex: Int = 0,
    count: Int = 15
) = withContext(Dispatchers.Default) {
    with(searchParams) {
        parse(
            token.username,
            session.post(
                route { courseListRoute(sort) },
                form(pageIndex, count)
            ) {
                parameter("kcxx", name.encodeURLParameter())
                parameter("skls", teacher.encodeURLParameter())
                parameter("skxq", dayOfWeek?.value ?: "")
                parameter("skjc", section.value.ifNotBlank { "$it-" })
                parameter("sfym", emptyOnly)
                parameter("sfct", filterConflicting)
                if (sort is Sort.General) {
                    parameter("xq", campus?.id ?: "")  // 校区
                }
            },
            sort
        )
    }
}


/**
 * 搜索参数
 * @param name 课程名
 * @param teacher 教师
 * @param dayOfWeek 星期几
 * @param section 节次
 * @param emptyOnly 过滤已满
 * @param filterConflicting 过滤冲突课程
 * @param campus 校区
 */
data class SearchParams(
    val name: String = "",
    val teacher: String = "",
    val dayOfWeek: DayOfWeek? = null,
    val section: Section = Section.Unspecified,
    val emptyOnly: Boolean = false,
    val filterConflicting: Boolean = false,
    val campus: Campus? = null
)


/**
 * 节次
 * @property value
 */
enum class Section(pair: Pair<Int, Int>? = null) {
    Unspecified,
    First(1 to 2), Second(3 to 4),
    Third(5 to 6), Fourth(7 to 8),
    Fifth(9 to 10), Sixth(11 to 12);

    val value = pair?.run { "${pair.first}-${pair.second}" } ?: ""
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
 * 将 [response] 解析为 [Course] 列表
 * @param response
 * @return [List]<[Course]>
 */
private suspend fun parse(username: String, response: HttpResponse, sort: Sort): List<Course> {
    val rawCourses = try {
        response.bodyAsText().json.getJSONArray("aaData")
    }catch (e: Throwable) {
        return emptyList()
    }

    val courses = arrayListOf<Course>()
    for (index in 0..<rawCourses.length()) {
        rawCourses.getJSONObject(index).apply {
            courses.add(
                Course(
                    username = username,
                    operateId = getString("jx0404id"),
                    courseId = getString("kch"),
                    name = getString("kcmc"),
                    teacher = getString("skls").fixTeacherName(),
                    point = getString("xf"),
                    campus = Campus.getById(getInt("xqid")),
                    time = getString("sksj").noHtmlSyntax(),
                    area = getString("skdd").noHtmlSyntax(),
                    total = getString("xxrs"),
                    remaining = getString("syrs"),
                    status = try {
                        getString("ctsm")
                    } catch (e: Throwable) {
                        ""
                    },
                    department = getString("dwmc"),
                    classHours = getString("zxs"),
                    examMode = try {
                        getString("khfs")
                    } catch (e: Throwable) {
                        null
                    },
                    selectable = getInt("sfkfxk") == 1,
                    selected = mutableStateOf(getInt("sfYx") == 1),
                    sort = sort
                )
            )
        }
    }

    return courses
}


/**
 * 剔除 html 语法
 * @receiver [String]
 * @return [String]
 */
private fun String.noHtmlSyntax() = replace("&nbsp;", "").replace("<br>", "\n")


/**
 * 课程
 * @property username 学号
 * @property operateId 操作 id
 * @property courseId 课程编号
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
@Serializable
class Course(
    val username: String,
    val operateId: String,
    val courseId: String,
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
    val selected: @Serializable(with = MutableStateBooleanSerializer::class) MutableState<Boolean>,
    val sort: Sort
) : Identified() {
    override val id = username + operateId
}