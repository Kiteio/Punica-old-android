package org.kiteio.punica.edu.system.api.course

import org.kiteio.punica.candy.route
import org.kiteio.punica.edu.system.CourseSystem

/**
 * 退出选课系统
 * @receiver [CourseSystem]
 */
suspend fun CourseSystem.exit() {
    session.post(CourseSystem.route { EXIT })
}