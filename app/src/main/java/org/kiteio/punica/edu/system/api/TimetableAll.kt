package org.kiteio.punica.edu.system.api

import com.fleeksoft.ksoup.Ksoup
import io.ktor.client.statement.bodyAsText
import io.ktor.http.parameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import org.kiteio.punica.datastore.Identified
import org.kiteio.punica.edu.foundation.Semester
import org.kiteio.punica.edu.system.EduSystem
import org.kiteio.punica.edu.system.EduSystem.Companion.semester
import java.util.regex.Pattern

/**
 * 全校课表
 * @receiver [EduSystem]
 * @return [TimetableAll]
 */
suspend fun EduSystem.timetableAll() = withContext(Dispatchers.Default) {
    val text = session.post(
        route { TIMETABLES },
        parameters {
            append("xnxqh", semester.toString())
        }
    ).bodyAsText()

    val matcher = Pattern.compile("<tr>.*?</tr>", Pattern.DOTALL).matcher(text)

    matcher.apply { find(); find() }  // 去掉表头
    val regex = Regex("^(.+?)?\\s*\\((.*?)\\)$")  // 匹配教师和周次
    val map = HashMap<String, List<TimetableAllItem>>()

    while (matcher.find()) {
        val document = Ksoup.parseBodyFragment(matcher.group())
        val elements = document.body().children()

        val items = arrayListOf<TimetableAllItem>()
        for (index in 1..<elements.size) {
            // 空格子
            if (elements[index].childrenSize() == 0) {
                continue
            }

            val texts = elements[index].child(0).textNodes().map { it.text().trim() }
            val section = ((index - 1) % 6 * 2 + 1).let { setOf(it, it + 1) }
            val dayOfWeek = (index - 1) / 6 + 1

            // 一个格子
            for (itemIndex in texts.indices step 3) {
                val destructed = regex.find(texts[itemIndex + 1])!!.destructured
                val weeksStr = destructed.component2()

                items.add(
                    TimetableAllItem(
                        teacher = destructed.component1(),
                        weeksStr = weeksStr,
                        weeks = parseWeek(weeksStr),
                        area = texts[itemIndex + 2],
                        section = section,
                        dayOfWeek = dayOfWeek,
                        clazz = texts[itemIndex]
                    )
                )
            }
        }

        map[elements[0].text()] = items
    }

    return@withContext TimetableAll(semester, map)
}


/**
 * 全校课表
 * @property semester 学期
 * @property map
 */
@Serializable
class TimetableAll(
    val semester: Semester,
    val map: HashMap<String, List<TimetableAllItem>>
) : Identified() {
    override val id = semester.toString()
}


/**
 * 全校课表项
 * @property teacher 教师
 * @property weeksStr 原始周次
 * @property weeks 周次
 * @property area 地点
 * @property section 节次
 * @property dayOfWeek 星期几 1..7
 * @property clazz 班级名
 */
@Serializable
data class TimetableAllItem(
    override val teacher: String,
    override val weeksStr: String,
    override val weeks: Set<Int>,
    override val area: String,
    override val section: Set<Int>,
    override val dayOfWeek: Int,
    override val clazz: String,
) : TimetableItemLike