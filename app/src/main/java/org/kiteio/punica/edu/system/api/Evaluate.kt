package org.kiteio.punica.edu.system.api

import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.nodes.Element
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ParametersBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.kiteio.punica.R
import org.kiteio.punica.edu.system.EduSystem
import org.kiteio.punica.getString

/**
 * 教评列表
 * @receiver [EduSystem]
 * @return [List]<[EvaluateItem]>
 */
suspend fun EduSystem.evaluateList(): List<EvaluateItem> = withContext(Dispatchers.Default) {
    val text = session.fetch(route { EVALUATE_LIST }).bodyAsText()

    val document = Ksoup.parse(text)
    val table = document.getElementsByClass("Nsb_r_list Nsb_table")[0]
    val rows = table.getElementsByTag("tr")

    if (rows.size > 1) {
        val elements = rows[1].getElementsByTag("td")[6].getElementsByTag("a")

        val items = arrayListOf<EvaluateItem>()
        for (element in elements) {
            val sort = element.text()

            val innerRows = session.fetch(route { element.attr("href") })
                .bodyAsText().run { Ksoup.parse(this@run) }
                .getElementById("dataList")!!
                .getElementsByTag("tr")

            for (index in 1..<innerRows.size) {
                val infos = innerRows[index].getElementsByTag("td")
                val route = infos[7].getElementsByTag("a").first()!!.attr("onclick")

                items.add(
                    EvaluateItem(
                        id = infos[1].text(),
                        name = infos[2].text(),
                        teacher = infos[3].text(),
                        sort = sort,
                        route = route.ifBlank { null }?.run { substring(7, length - 12) }
                    )
                )
            }
        }

        return@withContext items
    } else error(getString(R.string.evaluation_closed))
}


/**
 * 评教
 * @receiver [EduSystem]
 * @param evaluateItem
 * @param submit true 提交  false 保存
 * @param isNegative 是否差评
 */
suspend fun EduSystem.evaluate(evaluateItem: EvaluateItem, submit: Boolean, isNegative: Boolean) {
    evaluateItem.route ?: return

    val text = session.fetch(route { evaluateItem.route }).bodyAsText()

    val document = Ksoup.parse(text)
    val table = document.getElementById("Form1")!!
    val elements = table.children()

    val parametersBuilder = ParametersBuilder()

    for (index in 0..<elements.size - 2) {
        val key = elements[index].attr("name")
        val value = if (key == "issubmit") (if (submit) "1" else "0") else
            elements[index].attr("value")

        parametersBuilder.append(key, value)
    }

    val rows = table.getElementById("table1")!!
        .getElementsByTag("tr")
    val option = if (isNegative) 3 else 1

    for (index in 1..<rows.size) {
        val infos = rows[index].children()

        if (infos.size == 2) {
            parametersBuilder.append(infos[0].child(0))

            val options = infos[1].children()
            for (optionIndex in 1..<options.size step 2) {
                if (optionIndex == option) {
                    parametersBuilder.append(options[0])
                }

                parametersBuilder.append(options[optionIndex])
            }
        }
    }

    session.post(route { EVALUATE }, parametersBuilder.build())
}


/**
 * 将 [element] key value 属性添加至 [ParametersBuilder]
 * @receiver [ParametersBuilder]
 * @param element
 */
private fun ParametersBuilder.append(element: Element) =
    with(element) { append(attr("name"), attr("value")) }


/**
 * 教评项
 * @property id 课程编号
 * @property name 课程名
 * @property teacher 教师
 * @property sort 课程性质
 * @property route 评教路由，null 表示已评价
 */
data class EvaluateItem(
    val id: String,
    val name: String,
    val teacher: String,
    val sort: String,
    val route: String?
)