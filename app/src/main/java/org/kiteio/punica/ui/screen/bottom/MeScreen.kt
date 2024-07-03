package org.kiteio.punica.ui.screen.bottom

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import org.kiteio.punica.R
import org.kiteio.punica.getString

/**
 * 我
 */
@Composable
fun MeScreen() {
    Text(text = getString(R.string.me))
}