package org.kiteio.punica.candy

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.withStyle

/**
 * 添加可点击文字
 * @receiver [AnnotatedString.Builder]
 * @param text
 * @param style
 * @param onClick
 */
fun AnnotatedString.Builder.appendClickable(
    text: String,
    style: TextStyle? = null,
    onClick: () -> Unit
) {
    val start = length
    style?.let { withStyle(it.toSpanStyle()) { append(text) } }?: append(text)
    addLink(LinkAnnotation.Clickable(text) { onClick() }, start, text.length)
}