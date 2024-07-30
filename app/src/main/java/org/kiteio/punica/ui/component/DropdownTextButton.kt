package org.kiteio.punica.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import org.kiteio.punica.ui.dp4


@Composable
fun DropdownMenuItem(
    text: @Composable () -> Unit,
    onClick: () -> Unit,
    selected: Boolean = false,
    trailingIcon: @Composable (() -> Unit)? = null,
) {
    DropdownMenuItem(
        text = {
            CompositionLocalProvider(
                value = LocalTextStyle provides LocalTextStyle.current.run {
                    if (selected) copy(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    ) else this
                }
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AnimatedVisibility(visible = selected) {
                        Row {
                            Text(text = "▸")
                            Spacer(modifier = Modifier.width(dp4(2)))
                        }
                    }
                    text()
                }
            }
        },
        onClick = onClick,
        trailingIcon = trailingIcon
    )
}