package org.kiteio.punica.ui.component

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.buildAnnotatedString
import org.kiteio.punica.candy.appendClickable

/**
 * [Checkbox]
 * @param checked
 * @param onCheckedChange
 * @param label
 * @param reverse 复选框和 [label] 是否反向排列
 */
@Composable
fun CheckBox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    label: String? = null,
    reverse: Boolean = false
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        if (reverse) {
            label?.let {
                Text(
                    text = buildAnnotatedString { appendClickable(label) { onCheckedChange(!checked) } }
                )
            }
            Checkbox(checked = checked, onCheckedChange = onCheckedChange)
        } else {
            Checkbox(checked = checked, onCheckedChange = onCheckedChange)
            label?.let {
                Text(
                    text = buildAnnotatedString { appendClickable(label) { onCheckedChange(!checked) } }
                )
            }
        }
    }
}