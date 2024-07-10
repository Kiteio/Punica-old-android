package org.kiteio.punica.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalFocusManager

/**
 * [Scaffold] 嵌套 [Column]
 * @param modifier
 * @param innerModifier
 * @param verticalArrangement
 * @param horizontalAlignment
 * @param topBar
 * @param bottomBar
 * @param floatingActionButton
 * @param contentWindowInsets
 * @param content
 */
@Composable
fun ScaffoldColumn(
    modifier: Modifier = Modifier,
    innerModifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    contentWindowInsets: WindowInsets = ScaffoldDefaults.contentWindowInsets,
    content: @Composable ColumnScope.() -> Unit
) {
    Scaffold(
        modifier = modifier.focusCleaner(),
        topBar = topBar,
        bottomBar = bottomBar,
        floatingActionButton = floatingActionButton,
        contentWindowInsets = contentWindowInsets
    ) { innerPadding ->
        Column(
            modifier = innerModifier.padding(innerPadding),
            verticalArrangement = verticalArrangement,
            horizontalAlignment = horizontalAlignment,
            content = content
        )
    }
}


/**
 * [Scaffold] 嵌套 [Box]
 * @param modifier
 * @param innerModifier
 * @param contentAlignment
 * @param topBar
 * @param bottomBar
 * @param floatingActionButton
 * @param contentWindowInsets
 * @param content
 */
@Composable
fun ScaffoldBox(
    modifier: Modifier = Modifier,
    innerModifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.TopStart,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    contentWindowInsets: WindowInsets = ScaffoldDefaults.contentWindowInsets,
    content: @Composable BoxScope.() -> Unit
) {
    Scaffold(
        modifier = modifier.focusCleaner(),
        topBar = topBar,
        bottomBar = bottomBar,
        floatingActionButton = floatingActionButton,
        contentWindowInsets = contentWindowInsets
    ) { innerPadding ->
        Box(
            modifier = innerModifier.padding(innerPadding),
            contentAlignment = contentAlignment,
            content = content
        )
    }
}


/**
 * 点击可清除焦点
 * @receiver [Modifier]
 * @param focusManager
 * @return [Modifier]
 */
@Composable
private fun Modifier.focusCleaner(
    focusManager: FocusManager = LocalFocusManager.current
) = this.clickable(
    interactionSource = remember{ MutableInteractionSource() },
    indication = null
) { focusManager.clearFocus() }