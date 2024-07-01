package org.kiteio.punica.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.ViewModel
import org.kiteio.punica.edu.system.EduSystem

/**
 * 应用级 [ViewModel]
 * @property eduSystem EduSystem?
 */
class ApplicationViewModel : ViewModel() {
    var eduSystem by mutableStateOf<EduSystem?>(null)
        private set


    /**
     * 已登录教务系统
     * @param eduSystem
     */
    fun onLoggedIn(eduSystem: EduSystem) {
        this.eduSystem = eduSystem
    }


    suspend fun onLogout() {
        eduSystem?.logout()
        eduSystem = null
    }
}


/**
 * [ApplicationViewModel]
 */
val LocalViewModel = staticCompositionLocalOf<ApplicationViewModel> {
    error("CompositionLocal ${ApplicationViewModel::class.simpleName} not present")
}