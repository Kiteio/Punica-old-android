package org.kiteio.punica.ui.screen

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import org.kiteio.punica.getString
import org.kiteio.punica.ui.component.NavBackTopAppBar
import org.kiteio.punica.ui.component.ScaffoldColumn
import org.kiteio.punica.ui.navigation.Route

/**
 * 账号
 */
@Composable
fun AccountScreen() {
    ScaffoldColumn(
        topBar = { NavBackTopAppBar(route = Route.Account) }
    ) {
        Text(text = getString(Route.Account.nameResId))
    }
}