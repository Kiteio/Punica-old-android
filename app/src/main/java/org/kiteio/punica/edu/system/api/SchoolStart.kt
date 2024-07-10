package org.kiteio.punica.edu.system.api

import com.fleeksoft.ksoup.Ksoup
import io.ktor.client.statement.bodyAsText
import org.kiteio.punica.R
import org.kiteio.punica.edu.system.EduSystem
import org.kiteio.punica.getString
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * 开学日期
 * @receiver [EduSystem]
 * @return [LocalDate]
 */
suspend fun EduSystem.schoolStart(): LocalDate {
    val text = session.fetch(route { SCHOOL_START }).bodyAsText()

    val document = Ksoup.parse(text)
    val table = document.getElementById("kbtable")!!
    val rows = table.getElementsByTag("tr")

    return LocalDate.parse(
        rows[1].getElementsByTag("td")[2].attr("title").apply {
            ifBlank { error(getString(R.string.calendar_not_updated)) }
        },
        DateTimeFormatter.ofPattern("yyyy年MM月dd", Locale.CHINA)
    )
}