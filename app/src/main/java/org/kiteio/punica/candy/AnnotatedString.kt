package org.kiteio.punica.candy

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.TextLinkStyles

/**
 * 添加可点击文字
 * @receiver [AnnotatedString.Builder]
 * @param text
 * @param style
 * @param onClick
 */
fun AnnotatedString.Builder.appendClickable(
    text: String,
    style: TextLinkStyles? = null,
    onClick: () -> Unit
) {
    val start = length
    append(text)
    addLink(LinkAnnotation.Clickable(text, styles = style) { onClick() }, start, text.length)
}