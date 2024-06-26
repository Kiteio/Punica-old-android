package org.kiteio.punica.edu.system.api

import com.fleeksoft.ksoup.Ksoup
import io.ktor.client.statement.bodyAsText
import io.ktor.http.parameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.kiteio.punica.edu.system.EduSystem
import org.kiteio.punica.edu.system.EduSystem.Companion.semester
import java.util.regex.Pattern

/**
 * 全校课表
 * @receiver [EduSystem]
 * @return [HashMap]<[String], [List]<[List]<[TimetablesItem]>?>>
 */
suspend fun EduSystem.timetables() = withContext(Dispatchers.Default) {
    val text = session.post(
        route { TIMETABLES },
        parameters {
            append("xnxqh", semester)
        }
    ).bodyAsText()

    val matcher = Pattern.compile("<tr>.*?</tr>", Pattern.DOTALL).matcher(text)

    matcher.apply { find(); find() }  // 去掉表头
    val regex = Regex("^(.+?)?\\s*\\((.*?)\\)$")  // 匹配教师和周次
    val timetables = HashMap<String, List<List<TimetablesItem>?>>()

    while (matcher.find()) {
        val document = Ksoup.parseBodyFragment(matcher.group())
        val elements = document.body().children()

        val itemsList = arrayListOf<List<TimetablesItem>?>()
        for (index in 1..<elements.size) {
            // 空格子
            if (elements[index].childrenSize() == 0) {
                itemsList.add(null); continue
            }

            val texts = elements[index].child(0).textNodes().map { it.text().trim() }
            val section = ((index - 1) % 6 * 2 + 1).let { setOf(it, it + 1) }
            val dayOfWeek = (index - 1) / 6 + 1

            // 一个格子
            val items = arrayListOf<TimetablesItem>()
            for (itemIndex in texts.indices step 3) {
                val destructed = regex.find(texts[itemIndex + 1])!!.destructured
                val weekStr = destructed.component2()

                items.add(
                    TimetablesItem(
                        teacher = destructed.component1(),
                        weekStr = weekStr,
                        week = parseWeek(weekStr),
                        area = texts[itemIndex + 2],
                        section = section,
                        dayOfWeek = dayOfWeek,
                        clazz = texts[itemIndex]
                    )
                )
            }

            itemsList.add(items)
        }

        timetables[elements[0].text()] = itemsList
    }

    return@withContext timetables
}


/**
 * 全校课表项
 * @property teacher 教师
 * @property weekStr 原始周次
 * @property week 周次
 * @property area 地点
 * @property section 节次
 * @property dayOfWeek 星期几 1..7
 * @property clazz 班级名
 */
data class TimetablesItem(
    override val teacher: String,
    override val weekStr: String,
    override val week: Set<Int>,
    override val area: String,
    override val section: Set<Int>,
    override val dayOfWeek: Int,
    override val clazz: String,
) : TimetableItemLike