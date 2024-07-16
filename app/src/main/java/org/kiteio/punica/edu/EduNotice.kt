package org.kiteio.punica.edu

import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.nodes.Element
import com.fleeksoft.ksoup.select.Elements
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import org.kiteio.punica.candy.API
import org.kiteio.punica.candy.Log
import org.kiteio.punica.candy.route
import org.kiteio.punica.request.fetch

/**
 * 教务通知
 */
object EduNotice : API {
    override val root = "https://jwc.gdufe.edu.cn"


    /**
     * 通知列表路由
     * @param index 页码，从 0 开始
     * @return [String]
     */
    private fun listRoute(index: Int) =
        "/4133/list${if (index != 0) index + 1 else ""}.htm"


    /**
     * 通知列表
     * @param index
     * @return [List]<[NoticeItem]>
     */
    suspend fun list(index: Int) = withContext(Dispatchers.Default) {
        val response = fetch(route { listRoute(index) })

        if (index == 10) Log.e(response.status)

        // 处理重定向
        val text = if (response.status == HttpStatusCode.MovedPermanently) {
            fetch(response.headers[HttpHeaders.Location]!!).bodyAsText()
        } else response.bodyAsText()

        val document = Ksoup.parse(text)
        val elements = document.getElementsByClass("news_list list2")[0]
            .getElementsByTag("li")

        val items = arrayListOf<NoticeItem>()
        for (element in elements) {
            element.getElementsByTag("a")[0].apply {
                items.add(
                    NoticeItem(
                        title = attr("title"),
                        time = element.child(1).text(),
                        url = fixURL(attr("href"))
                    )
                )
            }
        }

        return@withContext items
    }


    /**
     * 将 route 补全
     * @param route
     */
    private fun fixURL(route: String) =
        if (Regex("^http.*").matches(route)) route else route { route }


    /**
     * 通知详情
     * @param noticeItem
     * @return [String]
     */
    suspend fun notice(noticeItem: NoticeItem) = withContext(Dispatchers.Default) {
        val text = fetch(noticeItem.url).bodyAsText()

        val document = Ksoup.parse(text)
        val article = document.getElementsByClass("wp_articlecontent")[0]

        val pdf = article.getElementsByClass("wp_pdf_player")

        // 通知内容为 PDF 文件
        if (pdf.isNotEmpty())
            return@withContext Notice(noticeItem.url, fixURL(pdf[0].attr("pdfsrc")))

        // 通知内容为 Html
        val markdown = buildString {
            // 发布信息
            val info = document.getElementsByClass("arti_metas")[0]
            appendParagraph("## ${noticeItem.title}")
            appendParagraph(
                "> ${
                    info.children().apply { removeLast() }.joinToString { it.text() }
                }"
            )

            val h1Regex = Regex("^[一二三四五六七八九十]+[、·].*?")
            val h2Regex = Regex("^[(（][一二三四五六七八九十]+[）)].*?")

            // 解析段落
            article.children().forEach { paragraph ->
                val elements = paragraph.children()
                // 解析子标签
                var paragraphText = elements.parseChildText()

                if (paragraphText.isNotBlank()) {
                    if (paragraphText != noticeItem.title) {
                        if (h2Regex.matches(paragraphText)) {
                            paragraphText = "#### $paragraphText"
                        } else if (h1Regex.matches(paragraphText)) {
                            paragraphText = "### $paragraphText"
                        }
                        appendParagraph(paragraphText)
                    }
                }
            }
        }

        return@withContext Notice(noticeItem.url, markdown = markdown)
    }


    /**
     * 添加段落
     * @receiver [StringBuilder]
     * @param value
     */
    private fun StringBuilder.appendParagraph(value: String) {
        appendLine(value)
        appendLine()
    }


    /**
     * 解析子标签文字
     * @receiver [Elements]
     * @return [StringBuilder]
     */
    private fun Elements.parseChildText(): String {
        val stringBuilder = StringBuilder()
        // 解析子标签
        forEach { element ->
            when (element.tagName()) {
                "img" -> {
                    if (size == 1) {
                        stringBuilder.append(
                            "<img width='100%' src='${fixURL(element.attr("src"))}' />"
                        )
                    }
                }

                "a" -> {
                    element.ifTextNotBlank {
                        stringBuilder.append("[$it](${fixURL(element.attr("href"))})")
                    }
                }

                "table" -> {
                    buildString {
                        val rows = element.getElementsByTag("tr")

                        // 向表格添加 elements 内容，pair 为成对元素修饰
                        val appendRow = fun(elements: Elements, pair: String) {
                            appendLine(
                                "|${elements.joinToString("|") { "$pair${it.text()}$pair" }}|"
                            )
                        }

                        var lastLength = 0  // 判断是否需要创建新表格
                        for (row in rows) {
                            val items = row.getElementsByTag("td")

                            if (items.size != lastLength) {
                                // 创建新的表格
                                appendLine()
                                var spliter = "|"
                                for (index in 0..<items.size) {
                                    spliter += ":-:|"
                                }
                                appendRow(items, if (lastLength == 0) "" else "`")
                                appendLine(spliter)

                                lastLength = items.size
                            } else appendRow(items, "`")
                        }
                    }.also { stringBuilder.appendLine(it) }
                }

                else -> {
                    if (element.childrenSize() > 0) {
                        stringBuilder.append(element.children().parseChildText())
                        stringBuilder.append(element.ownText())
                    } else element.ifTextNotBlank { elementText ->
                        val linkRegex =
                            Regex("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}|https?://[a-zA-Z0-9./?=_%:-]+")
                        stringBuilder.append(
                            linkRegex.replace(elementText) { "<${it.value}>" }
                        )
                    }
                }
            }
        }

        return stringBuilder.toString()
    }


    /**
     * 如果 [Element.text] 不为空，执行 [block]
     * @receiver [Element]
     * @param block
     */
    private fun Element.ifTextNotBlank(block: (String) -> Unit) {
        text().run { if (isNotBlank()) block(this@run) }
    }
}


/**
 * 通知项
 * @property title 标题
 * @property time 发布时间
 * @property url
 */
@Serializable
data class NoticeItem(
    val title: String,
    val time: String,
    val url: String
)


/**
 * 通知详情
 * @property url
 * @property pdf PDF url
 * @property markdown Markdown 文本
 */
data class Notice(
    val url: String,
    val pdf: String? = null,
    val markdown: String? = null
)