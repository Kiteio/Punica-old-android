package org.kiteio.punica.edu.system

import com.fleeksoft.ksoup.Ksoup
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.kiteio.punica.R
import org.kiteio.punica.candy.ProxiedAPI
import org.kiteio.punica.candy.ProxiedAPIOwner
import org.kiteio.punica.candy.route
import org.kiteio.punica.edu.WebVPN
import org.kiteio.punica.edu.system.api.Token
import org.kiteio.punica.edu.system.api.course.Sort
import org.kiteio.punica.edu.system.api.token
import org.kiteio.punica.getString
import org.kiteio.punica.request.Session

/**
 * 选课系统
 * @property session
 * @property name 本轮选课名称
 * @property start 开始时间
 * @property end 结束时间
 * @property token
 * @property proxied 是否使用代理
 */
class CourseSystem private constructor(
    val session: Session,
    val name: String,
    val start: String,
    val end: String,
    private val token: Token,
    override val proxied: Boolean
) : ProxiedAPIOwner<CourseSystem.Companion>(Companion) {
    companion object : ProxiedAPI {
        override val agent = WebVPN
        override val root = EduSystem.root
        private const val BASE = EduSystem.BASE
        private const val GET_ENTRY = "$BASE/xsxk/xklc_list"  // 选课系统链接获取
        private const val ENTRY = "$BASE/xsxk/xsxk_index"  // 选课系统链接
        const val OVERVIEW = "$BASE/xsxk/xsxk_tzsm"  // 学分总览
        const val MY_COURSE = "$BASE/xsxkjg/comeXkjglb"  // 已选课程
        const val LOG = "$BASE/xsxkjg/getTkrzList"  // 退课日志
        const val DELETE = "$BASE/xsxkjg/xstkOper"  // 退课
        const val CAMPUS_CHECK = "$BASE/xsxkkc/checkXq"  // 校区检验
        const val PASSED_CHECK = "$BASE/xsxkkc/checkXscj"  // 成绩检验
        const val EXIT = "$BASE/xsxk/xsxk_exit"  // 退出系统


        /**
         * 课程列表
         * @param sort
         * @return [String]
         */
        fun courseListRoute(sort: Sort) = "$BASE/xsxkkc/xsxk${sort.listRoute}xk"


        /**
         * 选课
         * @param sort
         * @return [String]
         */
        fun courseSelectRoute(sort: Sort) = "$BASE/xsxkkc/${sort.selectRoute}xkOper"


        /**
         * 由 [eduSystem] 进入选课系统
         * @param eduSystem
         * @return [CourseSystem]
         */
        suspend fun from(eduSystem: EduSystem) = withContext(Dispatchers.Default) {
            with(eduSystem.session) {
                val text = fetch(route(eduSystem.proxied) { GET_ENTRY }).bodyAsText()
                val document = Ksoup.parse(text)
                val table = document.getElementById("tbKxkc")!!
                val rows = table.getElementsByTag("tr")

                if (rows.size > 1) {
                    val infos = rows[1].getElementsByTag("td")
                    val route = infos[6].child(0).attr("href")
                    fetch(route(eduSystem.proxied) { route })

                    return@with CourseSystem(
                        eduSystem.session,
                        infos[1].text().replace(infos[0].text(), ""),
                        infos[2].text(),
                        infos[3].text(),
                        eduSystem.token(route.split("jx0502zbid=")[1]),
                        eduSystem.proxied
                    )
                }
                error(getString(R.string.course_system_closed))
            }
        }


        /**
         * 由 [eduSystem] 和 [token] 进入选课系统
         * @param eduSystem
         * @param token 选课系统 [Token]
         * @return [CourseSystem]
         */
        suspend fun from(eduSystem: EduSystem, token: Token) = withContext(Dispatchers.Default) {
            if (token.name != eduSystem.name) error("token.name != eduSystem.name")

            val text = eduSystem.session.fetch(
                route { ENTRY }
            ) { parameter("jx0502zbid", token.value) }.bodyAsText()

            if (Ksoup.parse(text).body().text() == "当前未开放选课，具体请查看学校选课通知！")
                error(getString(R.string.course_system_closed))

            return@withContext CourseSystem(
                eduSystem.session,
                "由 id 进入",
                "未知",
                "未知",
                token,
                eduSystem.proxied
            )
        }


        /**
         * 去除教师名最后的逗号
         * @receiver [String]
         * @return [String]
         */
        fun String.fixTeacherName() =
            if (endsWith(",")) substring(0..length - 2) else this
    }
}