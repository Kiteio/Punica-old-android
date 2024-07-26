package org.kiteio.punica.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalFocusManager

/**
 * 点击可清除焦点
 * @receiver [Modifier]
 * @param focusManager
 * @return [Modifier]
 */
@Composable
fun Modifier.focusCleaner(
    focusManager: FocusManager = LocalFocusManager.current
) = this.clickable(
    interactionSource = remember { MutableInteractionSource() },
    indication = null
) { focusManager.clearFocus() }