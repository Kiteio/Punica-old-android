package org.kiteio.punica.edu

import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.Cookie
import io.ktor.http.parameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import org.kiteio.punica.candy.ProxiedAPI
import org.kiteio.punica.candy.ProxiedAPIOwner
import org.kiteio.punica.candy.json
import org.kiteio.punica.candy.route
import org.kiteio.punica.request.Session

/**
 * 第二课堂
 * @property name 学号
 * @property id 第二课堂系统提供的 id
 * @property token
 * @property session
 * @property proxied 是否使用代理
 */
class SecondClass private constructor(
    private val name: String,
    private val id: String,
    private val token: String,
    private val session: Session,
    override val proxied: Boolean
) : ProxiedAPIOwner<SecondClass.Companion>(Companion) {
    /**
     * 成绩单
     * @return [SecondClassReport]
     */
    suspend fun report() = withContext(Dispatchers.Default) {
        val data = session.fetch(route { REPORT }) {
            parameter("para", "{'userId':$id}")
            token()
        }.arrayData()

        val items = arrayListOf<SecondClassReportItem>()
        data.forEachApply {
            items.add(
                SecondClassReportItem(
                    name = getString("classifyName"),
                    score = getDouble("classifyHours"),
                    requiredScore = getDouble("classifySchoolMinHours")
                )
            )
        }

        return@withContext SecondClassReport(name, items)
    }


    /**
     * 成绩单日志
     * @return [List]<[SecondClassLog]>
     */
    suspend fun log() = withContext(Dispatchers.Default) {
        val data = session.fetch(route { LOG }) {
            parameter("para", "{'sourceType':'','xueqiId':'','classId':''}")
            token()
        }.arrayData()

        val items = arrayListOf<SecondClassLog>()
        data.forEachApply {
            items.add(
                SecondClassLog(
                    name = getString("actName"),
                    sort = getString("className"),
                    score = getDouble("score"),
                    time = getLong("sendTime"),
                    term = getString("xueqiName").replace(")", "")
                )
            )
        }

        return@withContext items
    }


    /**
     * 活动列表
     * @return [List]<[SecondClassActivityItem]>
     */
    suspend fun activities() = withContext(Dispatchers.Default) {
        val list = session.fetch(route { ACTIVITIES }) {
            parameter("para", "{'cur':1,'size':10000}")
            token()
        }.jsonData().getJSONArray("records")

        val items = arrayListOf<SecondClassActivityItem>()
        list.forEachApply {
            items.add(
                SecondClassActivityItem(
                    id = getString("id"),
                    name = getString("name"),
                    sort = getString("category"),
                    score = getDouble("hours"),
                    start = getLong("startTime"),
                    end = getLong("endTime"),
                    organization = getString("orgName"),
                    logo = getString("logo"),
                    type = getString("typeName")
                )
            )
        }

        return@withContext items
    }


    /**
     * 活动详情
     * @param id
     * @return [SecondClassActivity]
     */
    suspend fun activity(id: String) = withContext(Dispatchers.Default) {
        session.fetch(route { ACTIVITY_INFO }) {
            parameter("para", "{'activityId':'$id'}")
            token()
        }.jsonData().run {
            SecondClassActivity(
                name = getString("name"),
                desc = getString("introduce"),
                sort = getString("className"),
                score = getDouble("hours"),
                area = getString("pitchAddress"),
                deadline = getString("senrollEndTime"),
                picture = getString("haibaoUrl"),
                organization = getString("zhubanName"),
                owner = getString("adminName"),
                phoneNumber = getString("adminContact"),
                teacher = getString("teacherName"),
                trainingHours = getInt("classHours"),
                start = getString("startTime"),
                end = getString("endTime"),
                submit = getInt("subJob") == 1,
                maxNum = getInt("peopleLimit"),
                num = getInt("peopleCount"),
                type = getString("typeName")
            )
        }
    }


    /**
     * 设置 token
     * @receiver [HttpRequestBuilder]
     */
    private fun HttpRequestBuilder.token() = header("X-Token", token)


    companion object : ProxiedAPI {
        override val agent = WebVPN
        override val root = "http://2ketang.gdufe.edu.cn"
        private const val LOGIN = "/apps/common/login"  // 登录
        private const val REPORT = "/apps/user/achievement/by-classify-list"  // 成绩单
        private const val LOG = "/apps/user/achievement/by-classify-list-detail"  // 成绩单日志
        private const val ACTIVITIES = "/apps/activityImpl/list/getActivityByUser"  // 活动
        private const val ACTIVITY_INFO = "/apps/activityImpl/detail"  // 活动详情


        /**
         * 登录
         * @param name 学号
         * @param pwd 密码
         * @param cookies Cookie
         * @param proxied 是否使用代理
         * @return [SecondClass]
         */
        suspend fun login(
            name: String,
            pwd: String,
            cookies: MutableSet<Cookie>,
            proxied: Boolean
        ) = withContext(Dispatchers.Default) {
            Session(cookies).run {
                val response = post(
                    route(proxied) { LOGIN },
                    parameters {
                        append(
                            "para",
                            "{'school':10018,'account':'${name}','password':'${pwd.ifBlank { name }}'}"
                        )
                    }
                )

                val json = response.bodyAsText().json
                val token = response.headers["X-token"]!!
                val id = json.getJSONObject("data").getString("id")

                SecondClass(name, id, token, this@run, proxied)
            }
        }


        /**
         * 获取 data 列表
         * @receiver [HttpResponse]
         * @return [JSONArray]
         */
        private suspend fun HttpResponse.arrayData() = bodyAsText().json.getJSONArray("data")


        /**
         * 获取 data json
         * @receiver [HttpResponse]
         * @return [JSONObject]
         */
        private suspend fun HttpResponse.jsonData() = bodyAsText().json.getJSONObject("data")


        /**
         * 遍历 [JSONArray]
         * @receiver [JSONArray]
         * @param block
         */
        private inline fun JSONArray.forEachApply(block: JSONObject.() -> Unit) {
            for (index in 0..<length()) block(getJSONObject(index))
        }
    }
}


/**
 * 第二课堂成绩单
 * @property username 学号
 * @property items
 */
class SecondClassReport(
    val username: String,
    val items: List<SecondClassReportItem>
)


/**
 * 第二课堂成绩单项
 * @property name 名称
 * @property score 分数
 * @property requiredScore 要求分数
 */
data class SecondClassReportItem(
    val name: String,
    val score: Double,
    val requiredScore: Double
)


/**
 * 成绩单日志
 * @property name 活动名
 * @property sort 分类
 * @property score 分数
 * @property time 时间戳
 * @property term 学期
 */
data class SecondClassLog(
    val name: String,
    val sort: String,
    val score: Double,
    val time: Long,
    val term: String
)


/**
 * 活动项
 * @property id
 * @property name 活动名
 * @property sort 分类
 * @property score 分数
 * @property start 开始时间戳
 * @property end 结束时间戳
 * @property organization 组织名
 * @property logo
 * @property type 类型
 */
data class SecondClassActivityItem(
    val id: String,
    val name: String,
    val sort: String,
    val score: Double,
    val start: Long,
    val end: Long,
    val organization: String,
    val logo: String,
    val type: String
)


/**
 * 活动详情
 * @property name 活动名
 * @property desc 描述
 * @property sort 分类
 * @property score 分数
 * @property area 地点
 * @property deadline 保命截止时间
 * @property picture 图片
 * @property organization 主办方
 * @property owner 管理员
 * @property phoneNumber 手机号
 * @property teacher 指导老师
 * @property trainingHours 培训时间
 * @property start 开始时间
 * @property end 结束时间
 * @property submit 是否必须提交作业
 * @property maxNum 最大人数
 * @property num 当前人数
 * @property type 类型
 */
data class SecondClassActivity(
    val name: String,
    val desc: String,
    val sort: String,
    val score: Double,
    val area: String,
    val deadline: String,
    val picture: String,
    val organization: String,
    val owner: String,
    val phoneNumber: String,
    val teacher: String,
    val trainingHours: Int,
    val start: String,
    val end: String,
    val submit: Boolean,
    val maxNum: Int,
    val num: Int,
    val type: String
)