package org.kiteio.punica.ui.screen.module

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import org.kiteio.punica.R
import org.kiteio.punica.getString
import org.kiteio.punica.ui.component.NavBackTopAppBar
import org.kiteio.punica.ui.component.ScaffoldBox
import org.kiteio.punica.ui.navigation.Route

/**
 * 课程成绩
 */
@Composable
fun SchoolReportScreen() {
    ScaffoldBox(topBar = { NavBackTopAppBar(route = Route.Module.SchoolReport) }) {
        Text(text = getString(R.string.school_report))
    }
}