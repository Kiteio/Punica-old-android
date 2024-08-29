package org.kiteio.punica.ui.screen

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.captionBar
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import org.kiteio.punica.getString
import org.kiteio.punica.ui.component.Icon
import org.kiteio.punica.ui.component.ScaffoldBox
import org.kiteio.punica.ui.navigation.NavHost
import org.kiteio.punica.ui.navigation.Route
import org.kiteio.punica.ui.navigation.composable
import org.kiteio.punica.ui.navigation.navigateTo

/**
 * 主页面
 */
@Composable
fun MainScreen() {
    val navController = rememberNavController()

    ScaffoldBox(
        bottomBar = { BottomBar(navController, Route.Bottom.values) },
        contentWindowInsets = WindowInsets.captionBar
    ) {
        NavHost(navController = navController, startRoute = Route.Bottom.Schedule) {
            composable(Route.Bottom.values)
        }
    }
}


/**
 * 底部导航
 * @param navController
 * @param routes
 */
@Composable
private fun BottomBar(navController: NavHostController, routes: List<Route.Bottom>) {
    val backStackEntry = navController.currentBackStackEntryAsState()
    val currentRouteId by remember { derivedStateOf { backStackEntry.value?.destination?.route } }

    NavigationBar {
        routes.forEach { route ->
            NavigationBarItem(
                selected = route.id == currentRouteId,
                onClick = {
                    if (currentRouteId != route.id) {
                        navController.popBackStack()
                        navController.navigateTo(route)
                    }
                },
                icon = { Icon(imageVector = route.icon) },
                label = { Text(text = getString(route.nameResId)) },
                alwaysShowLabel = false
            )
        }
    }
}