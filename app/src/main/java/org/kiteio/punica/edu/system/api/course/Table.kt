package org.kiteio.punica.edu.system.api.course

import com.fleeksoft.ksoup.Ksoup
import io.ktor.client.statement.*
import org.kiteio.punica.edu.system.CourseSystem


/**
 * 选课课表
 * @receiver [CourseSystem]
 * @return [List]<[String]?>
 */
suspend fun CourseSystem.table(): List<String?> = mutableListOf<String?>().apply {
    val document = Ksoup.parse(session.fetch(route { TABLE }).bodyAsText())
    val items = document.getElementsByTag("td")

    for (index in items.indices) {
        val text= items[index].text()

        add(
            if (index % 8 == 0) text.replace(",", "-").replace("节", "")
            else text.ifBlank { null }
        )
    }
}