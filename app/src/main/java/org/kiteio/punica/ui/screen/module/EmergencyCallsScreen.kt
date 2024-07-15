package org.kiteio.punica.ui.screen.module

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import org.kiteio.punica.R
import org.kiteio.punica.getString
import org.kiteio.punica.ui.component.NavBackTopAppBar
import org.kiteio.punica.ui.component.ScaffoldBox
import org.kiteio.punica.ui.navigation.Route

/**
 * 紧急电话
 */
@Composable
fun EmergencyCallsScreen() {
    ScaffoldBox(topBar = { NavBackTopAppBar(route = Route.Module.EmergencyCalls) }) {
        Text(text = getString(R.string.emergency_call))
    }
}