package org.kiteio.punica.ui.component

import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle

/**
 * 标题
 * @param text
 * @param modifier
 */
@Composable
fun Title(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        color = MaterialTheme.colorScheme.primary,
        style = MaterialTheme.typography.titleMedium,
        modifier = modifier
    )
}


/**
 * 弱化文本
 * @param text
 * @param modifier
 * @param color
 * @param style
 */
@Composable
fun SubduedText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = LocalContentColor.current.copy(0.7f),
    style: TextStyle = MaterialTheme.typography.bodySmall
) {
    Text(
        text = text,
        modifier = modifier,
        color = color,
        style = style,
    )
}