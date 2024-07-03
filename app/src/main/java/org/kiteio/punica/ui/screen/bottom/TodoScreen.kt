package org.kiteio.punica.ui.screen.bottom

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import org.kiteio.punica.R
import org.kiteio.punica.getString

/**
 * 待办
 */
@Composable
fun TodoScreen() {
    Text(text = getString(R.string.todo))
}