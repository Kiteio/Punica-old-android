package org.kiteio.punica.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.serialization.encodeToString
import org.kiteio.punica.Preferences
import org.kiteio.punica.Users
import org.kiteio.punica.candy.catching
import org.kiteio.punica.candy.launchCatching
import org.kiteio.punica.datastore.DefaultJson
import org.kiteio.punica.datastore.Keys
import org.kiteio.punica.edu.foundation.User
import org.kiteio.punica.edu.system.EduSystem
import org.kiteio.punica.edu.system.api.campusNetPwd
import org.kiteio.punica.edu.system.api.schoolStart

/**
 * 应用级 [ViewModel]
 * @property eduSystem
 */
class AppViewModel : ViewModel() {
    var eduSystem by mutableStateOf<EduSystem?>(null)
        private set


    /**
     * 已登录教务系统
     * @param eduSystem
     */
    fun onLoggedIn(eduSystem: EduSystem, user: User) {
        viewModelScope.launchCatching {
            onLogout()
            this@AppViewModel.eduSystem = eduSystem
            user.apply {
                campusNetPwd.ifBlank { campusNetPwd = eduSystem.campusNetPwd() }
            }
            Users.edit {
                it[stringPreferencesKey(user.name)] = DefaultJson.encodeToString(user)
            }
            Preferences.edit {
                it[Keys.lastUser] = user.name
                catching { it[Keys.schoolStart] = eduSystem.schoolStart().toString() }
            }
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