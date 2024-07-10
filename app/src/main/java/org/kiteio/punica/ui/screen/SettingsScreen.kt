package org.kiteio.punica.ui.screen

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import org.kiteio.punica.getString
import org.kiteio.punica.ui.component.NavBackTopAppBar
import org.kiteio.punica.ui.component.ScaffoldColumn
import org.kiteio.punica.ui.navigation.Route

/**
 * 设置
 */
@Composable
fun SettingsScreen() {
    ScaffoldColumn(
        topBar = { NavBackTopAppBar(route = Route.Settings) }
    ) {
        Text(text = getString(Route.Settings.nameResId))
    }
}