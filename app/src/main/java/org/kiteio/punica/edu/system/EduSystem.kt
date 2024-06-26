package org.kiteio.punica.edu.system

import com.fleeksoft.ksoup.Ksoup
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.readBytes
import io.ktor.http.parameters
import org.kiteio.punica.candy.ProxiedAPI
import org.kiteio.punica.candy.ProxiedAPIOwner
import org.kiteio.punica.candy.route
import org.kiteio.punica.candy.semester
import org.kiteio.punica.candy.text
import org.kiteio.punica.edu.WebVPN
import org.kiteio.punica.edu.foundation.User
import org.kiteio.punica.request.Session
import java.time.LocalDate

/**
 * 教务系统
 * @property user
 * @property session
 * @property proxied 是否使用代理
 * @property name 学号
 */
class EduSystem private constructor(
    private val user: User,
    val session: Session,
    override val proxied: Boolean
): ProxiedAPIOwner<EduSystem.Companion>(Companion) {
    val name get() = user.name


    companion object : ProxiedAPI {
        override val agent = WebVPN
        override val root = "http://jwxt.gdufe.edu.cn"
        const val BASE = "/jsxsd"
        private const val CAPTCHA = "$BASE/verifycode.servlet"  // 验证码
        private const val LOGIN = "$BASE/xk/LoginToXkLdap"  // 登录
        const val TIMETABLE = "$BASE/xskb/xskb_list.do"  // 课表
        const val TIMETABLES = "$BASE/kbcx/kbxx_kc_ifr"  // 全校课表
        const val EXAM_PLAN = "$BASE/xsks/xsksap_list"  // 考试安排
        const val SCHOOL_REPORT = "$BASE/kscj/cjcx_list"  // 课程成绩
        const val LEVEL_REPORT = "$BASE/kscj/djkscj_list"  // 等级成绩
        const val TEACHER_LIST = "$BASE/jsxx/jsxx_list"  // 教师列表
        const val TEACHER = "$BASE/jsxx/jsxx_query_detail"  // 教师
        const val SCHOOL_START = "$BASE/jxzl/jxzl_query"  // 开学日期
        const val EVALUATE_LIST = "$BASE/xspj/xspj_find.do"  // 教评列表
        const val EVALUATE = "$BASE/xspj/xspj_save.do"  // 评教
        const val PROGRESS = "$BASE/pyfa/xyjdcx"  // 学业进度
        const val PLAN = "$BASE/pyfa/pyfa_query"  // 执行计划
        const val CARD = "$BASE/grxx/xsxx"  // 学籍卡

        /**
         * 当前学期
         */
        val semester by lazy { LocalDate.now().semester }


        /**
         * 登录
         * @param user
         * @param proxied 是否使用代理
         * @return [EduSystem]
         */
        suspend fun login(user: User, proxied: Boolean) =
            login(user, Session(user.cookies), proxied)


        /**
         * 登录
         * @param user
         * @param session
         * @param proxied 是否使用代理
         * @param count 验证码错误重试次数
         * @return [EduSystem]
         */
        private suspend fun login(
            user: User,
            session: Session,
            proxied: Boolean,
            count: Int = 15
        ): EduSystem =
            with(user) {
                session.fetch(route(proxied) { BASE })

                val captcha = session.fetch(route(proxied) { CAPTCHA }).readBytes().text()
                val text = session.post(
                    route(proxied) { LOGIN },
                    parameters {
                        append("USERNAME", name)
                        append("PASSWORD", pwd)
                        append("RANDOMCODE", captcha)
                    }
                ).bodyAsText()

                if (text.isEmpty()) {
                    // 登录成功
                    EduSystem(this@with, session, proxied)
                } else {
                    val document = Ksoup.parse(text)
                    val title = document.title()

                    if (title == "广东财经大学综合教务管理系统-强智科技") {
                        val message = document.getElementsByTag("font")[0].text()

                        if (message == "验证码错误!!" && count > 0)
                            return@with login(user, session, proxied, count - 1)

                        error(message)
                    }
                    error(title)
                }
            }
    }
}