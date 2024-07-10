package org.kiteio.punica.ui.component

import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString

/**
 * 文本颜色能响应主题的 [ClickableText]
 * @param text
 * @param onClick
 * @param modifier
 */
@Composable
fun ClickableText(
    text: String,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    org.kiteio.punica.ui.component.ClickableText(
        text = buildAnnotatedString { append(text) },
        onClick = onClick,
        modifier = modifier
    )
}


/**
 * 文本颜色能响应主题的 [ClickableText]
 * @param text
 * @param onClick
 * @param modifier
 */
@Composable
fun ClickableText(
    text: AnnotatedString,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    ClickableText(
        text = buildAnnotatedString { append(text) },
        modifier = modifier,
        style = LocalTextStyle.current.copy(LocalContentColor.current),
        onClick = onClick
    )
}