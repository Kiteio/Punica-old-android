package org.kiteio.punica.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import io.ktor.client.network.sockets.ConnectTimeoutException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import org.kiteio.punica.candy.LocalDate
import org.kiteio.punica.candy.catching
import org.kiteio.punica.datastore.Keys
import org.kiteio.punica.datastore.Preferences
import org.kiteio.punica.datastore.Users
import org.kiteio.punica.datastore.set
import org.kiteio.punica.edu.WebVPN
import org.kiteio.punica.edu.foundation.Semester
import org.kiteio.punica.edu.foundation.User
import org.kiteio.punica.edu.system.EduSystem
import org.kiteio.punica.edu.system.api.campusNetPwd
import org.kiteio.punica.edu.system.api.schoolStart
import java.time.LocalDate

/**
 * 应用级 [ViewModel]
 * @property eduSystem
 */
class AppViewModel : ViewModel() {
    var eduSystem by mutableStateOf<EduSystem?>(null)
        private set


    /**
     * 登录
     * @param user
     * @return [Flow]<[EduSystem]>
     */
    suspend fun login(user: User) = flow {
        flow {
            emit(EduSystem.login(user, false))
        }.cancellable().catch {
            if (it is ConnectTimeoutException) {
                WebVPN.login(user.name, user.pwd, user.cookies)
                emit(EduSystem.login(user, true))
            } else throw it
        }.collect { eduSystem ->
            onLogout()
            this@AppViewModel.eduSystem = eduSystem

            user.apply {
                campusNetPwd.ifBlank { campusNetPwd = eduSystem.campusNetPwd() }
            }

            Users.edit { it.set(user) }
            Preferences.edit {
                it[Keys.lastUsername] = user.name

                val old =
                    it[Keys.schoolStart]?.let { schoolStart -> LocalDate.parse(schoolStart) }

                // 在 本地无开学日期 或 旧日期为旧学期 时更新
                if (old == null || !(old `in` EduSystem.semester)) catching {
                    eduSystem.schoolStart()?.run {
                        it[Keys.schoolStart] = this@run.toString()
                    }
                }
            }

            emit(eduSystem)
        }
    }


    /**
     * 退出登录
     */
    suspend fun onLogout() {
        eduSystem?.logout()
        eduSystem = null
    }
}


/**
 * 判断 [LocalDate] 是否在 [semester] 代表的学期区间内
 * @receiver [LocalDate]
 * @param semester
 * @return [Boolean]
 */
private infix fun LocalDate.`in`(semester: Semester) = when (year) {
    // 学年开始
    semester.year -> semester.term == 1 && monthValue in 8..12
    // 学年结束，其中 1 月属于第一学期
    semester.year + 1 -> semester.term == 2 && monthValue in 2..7 ||
            semester.term == 1 && monthValue == 1

    else -> false
}