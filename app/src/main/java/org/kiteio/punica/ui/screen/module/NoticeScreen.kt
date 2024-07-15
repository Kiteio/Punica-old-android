package org.kiteio.punica.ui.screen.module

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import org.kiteio.punica.R
import org.kiteio.punica.getString
import org.kiteio.punica.ui.component.NavBackTopAppBar
import org.kiteio.punica.ui.component.ScaffoldBox
import org.kiteio.punica.ui.navigation.Route

/**
 * 教学通知
 */
@Composable
fun NoticeScreen() {
    ScaffoldBox(topBar = { NavBackTopAppBar(route = Route.Module.Notice) }) {
        Text(text = getString(R.string.notice))
    }
}