package org.kiteio.punica.edu.system.api

import com.fleeksoft.ksoup.Ksoup
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import org.kiteio.punica.candy.isEven
import org.kiteio.punica.candy.isOdd
import org.kiteio.punica.datastore.Identified
import org.kiteio.punica.edu.foundation.Semester
import org.kiteio.punica.edu.system.EduSystem

/**
 * 课表
 * @receiver [EduSystem]
 * @param semester 学期，可借助 LocalDate 扩展属性 semester 获取
 * @return [Timetable]
 */
suspend fun EduSystem.timetable(semester: Semester) = withContext(Dispatchers.Default) {
    val text = session.fetch(route { TIMETABLE }) {
        parameter("xnxq01id", semester)
    }.bodyAsText()

    val document = Ksoup.parse(text)
    val table = document.getElementById("kbtable")!!
    val rawItems = table.getElementsByTag("td")
    val numRegex = Regex("\\d+")

    val itemsList = arrayListOf<List<TimetableItem>?>()
    for (columnIndex in 0..6) for (rowIndex in 0..5) {
        val infos = rawItems[rowIndex * 7 + columnIndex].child(3)

        // 空格子
        if (infos.childrenSize() == 0) {
            itemsList.add(null)
            continue
        }

        val textNodes = infos.textNodes()  // 课程名、节次
        val childTextNodes = infos.children().textNodes()  // 教师、周次、地点

        // 单个格子的多个 Item
        val items = arrayListOf<TimetableItem>()
        for (index in textNodes.indices step 3) {
            val indexPlus1 = index + 1
            val section = numRegex.findAll(textNodes[indexPlus1].text()).map {
                it.value.toInt()
            }.toList()

            // 节次中包含了两个以上数字，并且第三个节次为当前位置的节次
            // 则在前一格的解析中已经包括，需要剔除
            if (section.size > 2 && section[2] == rowIndex * 2 + 1) {
                if (index == textNodes.lastIndex - 2) itemsList.add(null)
                else continue
            }

            val weeksStr = childTextNodes[indexPlus1].text()
            items.add(
                TimetableItem(
                    name = textNodes[index].text(),
                    teacher = childTextNodes[index].text(),
                    weeksStr = weeksStr,
                    weeks = parseWeek(weeksStr),
                    area = childTextNodes[index + 2].text(),
                    section = section.toSet(),
                    dayOfWeek = columnIndex + 1
                )
            )
        }
        itemsList.add(items.ifEmpty { null })
    }

    return@withContext Timetable(
        username = name,
        semester = semester,
        remark = rawItems.last()!!.text(),
        items = itemsList
    )
}


/**
 * 解析周次
 * @param weeksStr
 * @return [MutableSet]<[Int]>
 */
fun parseWeek(weeksStr: String) = mutableSetOf<Int>().apply {
    var firstNum = ""  // 读取到的第一个数字
    var secondNum = ""  // 读取到的第二个数字
    var isSecond = false  // 正在写入的是否为第二个数

    for (index in weeksStr.indices) {
        val char = weeksStr[index]

        if (char.isDigit()) {
            if (isSecond) secondNum += char
            else firstNum += char

            continue
        }

        when (char) {
            // 数字连接符，需要切换为写入第二个数
            '-' -> isSecond = true
            // 数字分隔符，结算此前输入
            ',', '，' -> {
                if (isSecond) {
                    addAll(firstNum.toInt()..secondNum.toInt())
                    isSecond = false
                    secondNum = ""
                } else add(firstNum.toInt())
                firstNum = ""
            }
            // 跳过空字符，括号
            ' ', '(', '（' -> continue
            // 单双周
            else -> {
                if (isSecond) {
                    val range = firstNum.toInt()..secondNum.toInt()
                    when (char) {
                        '周' -> addAll(range)
                        '单' -> addAll(range.filter { it.isOdd() })
                        '双' -> addAll(range.filter { it.isEven() })
                    }
                } else {
                    // 防止出现多余分隔符
                    if (firstNum.isNotEmpty()) add(firstNum.toInt())
                }

                // 绝大多数情况下已经是到末尾了
                break
            }
        }
    }
}


/**
 * 课表
 * @property username 学号
 * @property semester 学期
 * @property remark 备注
 * @property items
 * @property id [username] + [semester]
 */
@Serializable
class Timetable(
    val username: String,
    val semester: Semester,
    val remark: String,
    val items: List<List<TimetableItem>?>
) : Identified() {
    override val id = username + semester
}


/**
 * 课程项
 * @property name 课程名
 * @property teacher 教师
 * @property weeksStr 原始周次
 * @property weeks 周次
 * @property area 地点
 * @property section 节次
 * @property dayOfWeek 星期几 1..7
 * @property clazz 班级名
 */
@Serializable
data class TimetableItem(
    val name: String,
    override val teacher: String,
    override val weeksStr: String,
    override val weeks: Set<Int>,
    override val area: String,
    override val section: Set<Int>,
    override val dayOfWeek: Int,
    override val clazz: String? = null,
) : TimetableItemLike


/**
 * 课表项抽象
 * @property teacher 教师
 * @property weeksStr 原始周次
 * @property weeks 周次
 * @property area 地点
 * @property section 节次
 * @property dayOfWeek 星期几 1..7
 * @property clazz 班级名
 */
interface TimetableItemLike {
    val teacher: String
    val weeksStr: String
    val weeks: Set<Int>
    val area: String
    val section: Set<Int>
    val dayOfWeek: Int
    val clazz: String?
}