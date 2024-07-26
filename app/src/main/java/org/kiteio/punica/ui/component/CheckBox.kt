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
 */
@Composable
fun CheckBox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    label: String? = null
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(checked = checked, onCheckedChange = onCheckedChange)
        label?.let {
            Text(
                text = buildAnnotatedString { appendClickable(label) { onCheckedChange(!checked) } }
            )
        }
    }
}