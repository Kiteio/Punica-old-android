package org.kiteio.punica.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.kiteio.punica.R
import org.kiteio.punica.Toast
import org.kiteio.punica.candy.LocalDate
import org.kiteio.punica.candy.dateMillis
import org.kiteio.punica.getString
import org.kiteio.punica.ui.dp4
import java.time.LocalDate

/**
 * [AlertDialog]
 * @param title
 * @param text
 * @param onDismiss
 * @param confirmButton
 * @param dismissButton
 * @param contentHorizontalAlignment 内容水平对齐
 */
@Composable
fun Dialog(
    title: @Composable (() -> Unit)? = null,
    text: @Composable ColumnScope.() -> Unit,
    onDismiss: () -> Unit,
    confirmButton: @Composable () -> Unit = {
        TextButton(onClick = onDismiss) { Text(text = getString(R.string.close)) }
    },
    dismissButton: @Composable (() -> Unit)? = null,
    contentHorizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = confirmButton,
        dismissButton = dismissButton,
        title = title,
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = contentHorizontalAlignment,
                content = text
            )
        }
    )
}


/**
 * [Dialog] 可见性控制
 * @param visible
 * @param content
 */
@Composable
fun DialogVisibility(visible: Boolean, content: @Composable AnimatedVisibilityScope.() -> Unit) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(10)),
        exit = fadeOut(tween(10)),
        content = content
    )
}


/**
 * 日期选择
 * @param visible
 * @param onDismiss
 * @param onConfirm 会自动调用 [onDismiss]
 * @param title
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(
    visible: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (LocalDate) -> Unit,
    title: @Composable (() -> Unit)? = null,
    initialDate: LocalDate?
) {
    DialogVisibility(visible = visible) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = (initialDate ?: LocalDate.now()).dateMillis
        )

        DatePickerDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { onConfirm(LocalDate(it)) }
                        onDismiss()
                    }
                ) {
                    Text(text = getString(R.string.confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text(text = getString(R.string.cancel))
                }
            }
        ) {
            Spacer(modifier = Modifier.height(dp4(2)))
            DatePicker(
                state = datePickerState,
                title = null,
                headline = {
                    title?.apply {
                        Row {
                            Spacer(modifier = Modifier.width(dp4(4)))
                            invoke()
                        }
                    }
                }
            )
        }
    }
}


/**
 * 删除提示
 * @param visible
 * @param onDismiss
 * @param onConfirm
 * @param desc
 */
@Composable
fun DeleteDialog(visible: Boolean, onDismiss: () -> Unit, onConfirm: () -> Unit, desc: String?) {
    DialogVisibility(visible = visible) {
        Dialog(
            text = {
                Text(
                    text = getString(
                        R.string.make_sure_to_delete, desc?.let { " $it " } ?: "")
                )
            },
            onDismiss = onDismiss,
            confirmButton = {
                TextButton(onClick = { onConfirm(); Toast(R.string.deleted).show(); onDismiss() }) {
                    Text(text = getString(R.string.confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text(text = getString(R.string.cancel))
                }
            },
            contentHorizontalAlignment = Alignment.Start
        )
    }
}