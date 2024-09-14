package org.kiteio.punica.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.kiteio.punica.ui.applyLocalAlpha
import org.kiteio.punica.ui.dp4
import org.kiteio.punica.ui.subduedContentColor

/**
 * 标题
 * @param text
 * @param modifier
 * @param maxLines
 * @param style
 */
@Composable
fun Title(
    text: String,
    modifier: Modifier = Modifier,
    maxLines: Int = Int.MAX_VALUE,
    style: TextStyle = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp)
) {
    Text(
        text = text,
        modifier = modifier,
        color = MaterialTheme.colorScheme.primary.applyLocalAlpha(),
        overflow = TextOverflow.Ellipsis,
        maxLines = maxLines,
        style = style
    )
}


/**
 * 弱化文本
 * @param text
 * @param modifier
 * @param color
 * @param textAlign
 * @param maxLines
 * @param style
 */
@Composable
fun SubduedText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = subduedContentColor(),
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
    style: TextStyle = MaterialTheme.typography.bodySmall
) {
    Text(
        text = text,
        modifier = modifier,
        color = color,
        textAlign = textAlign,
        overflow = TextOverflow.Ellipsis,
        maxLines = maxLines,
        style = style,
    )
}


/**
 * 弱化文本
 * @param text
 * @param modifier
 * @param color
 * @param textAlign
 * @param style
 */
@Composable
fun SubduedText(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    color: Color = subduedContentColor(),
    textAlign: TextAlign? = null,
    style: TextStyle = MaterialTheme.typography.bodySmall
) {
    Text(
        text = text,
        modifier = modifier,
        color = color,
        textAlign = textAlign,
        style = style,
    )
}


/**
 * [Icon] [IconText]
 * @param text 文本内容
 * @param leadingIcon 首部图标
 * @param modifier
 * @param leadingText 首部文字
 * @param color
 * @param leadingColor
 * @param horizontalArrangement
 * @param maxLines
 * @param style
 */
@Composable
fun IconText(
    text: String,
    leadingIcon: ImageVector,
    modifier: Modifier = Modifier,
    leadingText: String? = null,
    color: Color = Color.Unspecified,
    leadingColor: Color = Color.Unspecified,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    maxLines: Int = Int.MAX_VALUE,
    style: TextStyle = LocalTextStyle.current
) {
    val myLeadingColor =
        leadingColor.takeOrElse { MaterialTheme.colorScheme.secondary.applyLocalAlpha() }

    Row(
        horizontalArrangement = horizontalArrangement,
        verticalAlignment = if (leadingText == null) Alignment.CenterVertically else Alignment.Top,
        modifier = modifier
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = leadingIcon,
                tint = myLeadingColor,
                modifier = Modifier.size(style.fontSize.value.dp)
            )
            Spacer(modifier = Modifier.width(dp4()))

            if (leadingText != null) Text(text = leadingText, color = myLeadingColor, style = style)
        }
        if (leadingText != null) Spacer(modifier = Modifier.width(dp4(3)))

        Text(
            text = text,
            color = color,
            style = style,
            overflow = TextOverflow.Ellipsis,
            maxLines = maxLines
        )
    }
}


/**
 * 含键值的 [Text]
 * @param key
 * @param value
 * @param maxLines
 * @param modifier
 * @param style
 */
@Composable
fun KVText(
    key: String,
    value: String,
    modifier: Modifier = Modifier,
    maxLines: Int = Int.MAX_VALUE,
    style: TextStyle = MaterialTheme.typography.bodySmall
) {
    Row(modifier = modifier) {
        Text(
            text = key,
            color = MaterialTheme.colorScheme.secondary.applyLocalAlpha(),
            style = style
        )
        Spacer(modifier = Modifier.width(dp4(1)))

        Text(
            text = value,
            style = style,
            maxLines = maxLines,
            overflow = TextOverflow.Ellipsis
        )
    }
}