package org.kiteio.punica.ui.screen.bottom

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import org.kiteio.punica.R
import org.kiteio.punica.getString
import org.kiteio.punica.ui.component.ScaffoldColumn

/**
 * 待办
 */
@Composable
fun TodoScreen() {
    ScaffoldColumn {
        Text(text = getString(R.string.todo))
    }
}