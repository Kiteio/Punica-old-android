package org.kiteio.punica.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Checklist
import androidx.compose.material.icons.rounded.Numbers
import androidx.compose.material.icons.rounded.SentimentSatisfied
import androidx.compose.material.icons.rounded.Token
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import org.kiteio.punica.R
import org.kiteio.punica.ui.screen.LoginScreen
import org.kiteio.punica.ui.screen.MainScreen
import org.kiteio.punica.ui.screen.bottom.MeScreen
import org.kiteio.punica.ui.screen.bottom.ModuleScreen
import org.kiteio.punica.ui.screen.bottom.ScheduleScreen
import org.kiteio.punica.ui.screen.bottom.TodoScreen

/**
 * 路由
 * @property content 页面内容
 * @property nameResId 标题字符串资源 id
 * @property icon 图标
 * @property id 唯一标识
 */
sealed class Route(
    val content: @Composable () -> Unit,
    @StringRes val nameResId: Int = R.string.app_name,
    val icon: ImageVector = Icons.Rounded.Numbers
) {
    val id = this::class.simpleName!!


    /** 主页面 */
    data object Main : Route({ MainScreen() })


    /** 登录页 */
    data object Login : Route({ LoginScreen() })


    /** 底部导航路由 */
    sealed class Bottom(
        composable: @Composable () -> Unit,
        @StringRes nameResId: Int,
        icon: ImageVector
    ) : Route(composable, nameResId, icon) {
        /** 日程 */
        data object Schedule :
            Bottom({ ScheduleScreen() }, R.string.schedule, Icons.Rounded.WbSunny)

        /** 待办 */
        data object Todo : Bottom({ TodoScreen() }, R.string.todo, Icons.Rounded.Checklist)

        /** 模块 */
        data object Module : Bottom({ ModuleScreen() }, R.string.module, Icons.Rounded.Token)

        /** 我 */
        data object Me : Bottom({ MeScreen() }, R.string.me, Icons.Rounded.SentimentSatisfied)
    }
}