package org.kiteio.punica.ui.screen.bottom

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import org.kiteio.punica.R
import org.kiteio.punica.getString
import org.kiteio.punica.ui.component.ScaffoldColumn

/**
 * 模块
 */
@Composable
fun ModuleScreen() {
    ScaffoldColumn {
        Text(text = getString(R.string.module))
    }
}