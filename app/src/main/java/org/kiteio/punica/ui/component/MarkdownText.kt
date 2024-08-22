package org.kiteio.punica.ui.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.jeziellago.compose.markdowntext.MarkdownText
import org.kiteio.punica.ui.Link

/**
 * [MarkdownText]
 * @param markdown
 * @param modifier
 * @param contentPadding
 */
@Composable
fun MarkdownText(
    markdown: String,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    LazyColumn(contentPadding = contentPadding, modifier = modifier) {
        item { MarkdownText(markdown = markdown, linkColor = Color.Link, isTextSelectable = true) }
    }
}