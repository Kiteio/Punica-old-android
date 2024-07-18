package org.kiteio.punica.ui.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.kiteio.punica.ui.applyLocalAlpha
import org.kiteio.punica.ui.dp4
import org.kiteio.punica.ui.subduedContentColor

/**
 * 标题
 * @param text
 * @param modifier
 * @param style
 */
@Composable
fun Title(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp)
) {
    Text(
        text = text,
        color = MaterialTheme.colorScheme.primary.applyLocalAlpha(),
        style = style,
        modifier = modifier
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
    text: String,
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
 * @param text
 * @param leadingIcon
 * @param leadingText
 * @param color
 */
@Composable
fun IconText(
    text: String,
    leadingIcon: ImageVector,
    leadingText: String? = null,
    color: Color = Color.Unspecified,
) {
    Row {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = leadingIcon,
                tint = color.takeOrElse { MaterialTheme.colorScheme.secondary.applyLocalAlpha() },
                modifier = Modifier.size(
                    LocalTextStyle.current.fontSize.value.dp
                )
            )
            Spacer(modifier = Modifier.width(dp4()))
            leadingText?.run {
                Text(text = "$leadingText  ", color = MaterialTheme.colorScheme.secondary.applyLocalAlpha())
            }
        }
        Text(
            text = text,
            color = color
        )
    }
}