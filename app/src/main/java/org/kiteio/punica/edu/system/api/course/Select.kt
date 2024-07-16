package org.kiteio.punica.edu.system.api.course

import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.kiteio.punica.candy.json
import org.kiteio.punica.edu.system.CourseSystem

/**
 * 选课
 * @receiver {CourseSystem}
 * @param operateId
 * @param sort
 * @param priority 选课志愿
 */
suspend fun CourseSystem.select(
    operateId: String,
    sort: Sort,
    priority: Priority?
) = withContext(Dispatchers.Default) {
    session.fetch(
        route { courseSelectRoute(sort) }
    ) {
        parameter("jx0404id", operateId)
        parameter("xkzy", priority?.value ?: "")
        parameter("trjf", "")
        parameter("cxxdlx", "1")
    }.bodyAsText().json.run {
        if (!getBoolean("success")) error(getString("message"))
    }
}


/**
 * 选课志愿
 * @property value
 */
sealed class Priority(val value: String) {
    data object First : Priority("1")
    data object Second : Priority("2")
    data object Third : Priority("3")
}