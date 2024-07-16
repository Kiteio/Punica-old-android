package org.kiteio.punica.ui.component

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

/**
 * [ModalBottomSheet]
 * @param visible
 * @param onDismiss
 * @param skipPartiallyExpanded
 * @param content
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheet(
    visible: Boolean,
    onDismiss: () -> Unit,
    skipPartiallyExpanded: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = skipPartiallyExpanded)

    if (visible) {
        ModalBottomSheet(
            onDismissRequest = {
                coroutineScope.launch { sheetState.hide() }.invokeOnCompletion { onDismiss() }
            },
            sheetState = sheetState,
            content = content
        )
    }
}