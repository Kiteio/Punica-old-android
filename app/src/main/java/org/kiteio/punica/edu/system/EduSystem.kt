package org.kiteio.punica.edu.system

import com.fleeksoft.ksoup.Ksoup
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.readBytes
import io.ktor.http.Cookie
import io.ktor.http.parameters
import org.kiteio.punica.candy.API
import org.kiteio.punica.candy.route
import org.kiteio.punica.candy.text
import org.kiteio.punica.edu.foundation.User
import org.kiteio.punica.request.Session
import org.kiteio.punica.request.fetch

class EduSystem private constructor(private val user: User, val session: Session) {
    val name get() = user.name


    companion object: API {
        override val root = "http://jwxt.gdufe.edu.cn"
        const val BASE = "/jsxsd"
        private const val CAPTCHA = "$BASE/verifycode.servlet"  // 验证码
        private const val LOGIN = "$BASE/xk/LoginToXkLdap"  // 登录
        const val TIMETABLE = "$BASE/xskb/xskb_list.do"  // 课表
        const val EXAM_PLAN = "$BASE/xsks/xsksap_list"  // 考试安排
        const val SCHOOL_REPORT = "$BASE/kscj/cjcx_list"  // 课程成绩
        const val LEVEL_REPORT = "$BASE/kscj/djkscj_list"  // 等级成绩


        /**
         * 登录
         * @param name 学号
         * @param pwd 门户密码
         * @param cookies Cookie
         * @return [EduSystem]
         */
        suspend fun login(name: String, pwd: String, cookies: MutableSet<Cookie>) =
            login(User(name, pwd, cookies = cookies))


        /**
         * 登录
         * @param user
         * @return [EduSystem]
         */
        suspend fun login(user: User) =
            login(user, Session(user.cookies).also { fetch(route { BASE }) })


        /**
         * 登录
         * @param user
         * @param session
         * @param count 验证码错误重试次数
         * @return [EduSystem]
         */
        private suspend fun login(user: User, session: Session, count: Int = 15): EduSystem = with(user) {
            val captcha = session.fetch(route { CAPTCHA }).readBytes().text()
            val text = session.post(
                route { LOGIN },
                parameters {
                    append("USERNAME", name)
                    append("PASSWORD", pwd)
                    append("RANDOMCODE", captcha)
                }
            ).bodyAsText()

            if (text.isEmpty()) {
                // 登录成功
                EduSystem(this@with, session)
            } else {
                val document = Ksoup.parse(text)
                val title = document.title()

                if (title == "广东财经大学综合教务管理系统-强智科技") {
                    val message = document.getElementsByTag("font")[0].text()

                    if (message == "验证码错误!!" && count > 0)
                        return@with login(user, session, count - 1)

                    error(message)
                }
                error(title)
            }
        }
    }
}