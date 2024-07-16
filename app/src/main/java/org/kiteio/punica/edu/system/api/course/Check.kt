package org.kiteio.punica.edu.system.api.course

import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.kiteio.punica.candy.json
import org.kiteio.punica.edu.system.CourseSystem
import java.util.Date

/**
 * 课程校区与所在校区是否一致
 * @receiver [CourseSystem]
 * @param operateId
 * @return [Boolean]
 */
suspend fun CourseSystem.isSameCampus(operateId: String) = withContext(Dispatchers.Default) {
    session.fetch(route { CAMPUS_CHECK }) { parameters(operateId) }.bodyAsText()
        .json.run { getInt("status") == 0 }
}


/**
 * 是否已经通过课程考核
 * @receiver [CourseSystem]
 * @param operateId
 * @return [Boolean]
 */
suspend fun CourseSystem.isPassed(operateId: String) = withContext(Dispatchers.Default) {
    session.fetch(route { PASSED_CHECK }) { parameters(operateId) }.bodyAsText()
        .json.run { getInt("status") != 0 }
}


/**
 * 参数
 * @receiver [HttpRequestBuilder]
 * @param operateId
 */
private fun HttpRequestBuilder.parameters(operateId: String) {
    parameter("jx0404id", operateId)
    parameter("_", Date().time)
}