package org.kiteio.punica.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import org.kiteio.punica.datastore.Preferences
import org.kiteio.punica.datastore.Users
import org.kiteio.punica.candy.catching
import org.kiteio.punica.candy.launchCatching
import org.kiteio.punica.datastore.Keys
import org.kiteio.punica.datastore.set
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
            Users.edit { it.set(user) }
            Preferences.edit {
                it[Keys.lastUsername] = user.name
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