package org.kiteio.punica.edu.system.api.course

import com.fleeksoft.ksoup.Ksoup
import io.ktor.client.statement.bodyAsText
import org.kiteio.punica.candy.route
import org.kiteio.punica.edu.system.CourseSystem
import kotlin.collections.List

/**
 * 学分总览
 * @return [Overview]
 */
suspend fun CourseSystem.overview(): Overview {
    val document = Ksoup.parse(session.fetch(CourseSystem.route { OVERVIEW }).bodyAsText())
    val table = document.getElementsByTag("table")[0]
    val rows = table.getElementsByTag("tr")

    val infos = document.getElementsByTag("div")[0].text()  // 提示信息
    val header = rows[1].getElementsByTag("td")  // 表头
    val limit = rows[2].getElementsByTag("td")  // 选课学分上限
    val have = rows[3].getElementsByTag("td")  // 已选学分

    val pointInfos = arrayListOf<PointInfo>()
    for (index in 1..<header.size) {
        pointInfos.add(PointInfo(header[index].text(), have[index].text(), limit[index].text()))
    }

    return Overview(infos, pointInfos)
}


/**
 * 学分总览
 * @property info 本轮选课提示信息
 * @property pointInfos 选课学分信息
 * @constructor
 */
data class Overview(val info: String, val pointInfos: List<PointInfo>)


/**
 * 选课学分信息
 * @property name 分类
 * @property have 已有学分
 * @property limit 学分限制
 * @constructor
 */
data class PointInfo(val name: String, val have: String, val limit: String)