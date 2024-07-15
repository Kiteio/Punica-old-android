package org.kiteio.punica.ui.screen.module

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import org.kiteio.punica.R
import org.kiteio.punica.getString
import org.kiteio.punica.ui.component.NavBackTopAppBar
import org.kiteio.punica.ui.component.ScaffoldBox
import org.kiteio.punica.ui.navigation.Route

/**
 * 校园网
 */
@Composable
fun CampusNetScreen() {
    ScaffoldBox(topBar = { NavBackTopAppBar(route = Route.Module.CampusNet) }) {
        Text(text = getString(R.string.campus_net))
    }
}