package org.kiteio.punica.edu.system.api

import com.fleeksoft.ksoup.Ksoup
import io.ktor.client.statement.bodyAsText
import io.ktor.http.parameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.kiteio.punica.R
import org.kiteio.punica.edu.system.EduSystem
import org.kiteio.punica.edu.system.EduSystem.Companion.semester
import org.kiteio.punica.getString
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * 开学日期
 * @receiver [EduSystem]
 * @return [LocalDate]
 */
suspend fun EduSystem.schoolStart(): LocalDate = withContext(Dispatchers.Default) {
    val text = session.post(
        route { SCHOOL_START },
        parameters { append("xnxq01id", semester.toString()) }
    ).bodyAsText()

    val document = Ksoup.parse(text)
    val table = document.getElementById("kbtable")!!
    val rows = table.getElementsByTag("tr")

    LocalDate.parse(
        rows[1].getElementsByTag("td")[2].attr("title").apply {
            ifBlank { error(getString(R.string.calendar_not_updated)) }
        },
        DateTimeFormatter.ofPattern("yyyy年MM月dd", Locale.CHINA)
    )
}