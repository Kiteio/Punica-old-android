package org.kiteio.punica.ui.screen.module

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import org.kiteio.punica.R
import org.kiteio.punica.getString
import org.kiteio.punica.ui.component.NavBackTopAppBar
import org.kiteio.punica.ui.component.ScaffoldBox
import org.kiteio.punica.ui.navigation.Route

/**
 * 教学评价
 */
@Composable
fun EvaluationScreen() {
    ScaffoldBox(topBar = { NavBackTopAppBar(route = Route.Module.Evaluation) }) {
        Text(text = getString(R.string.evaluation))
    }
}