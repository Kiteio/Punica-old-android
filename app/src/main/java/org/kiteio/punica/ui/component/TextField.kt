package org.kiteio.punica.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.KeyboardActionScope
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import org.kiteio.punica.candy.collectAsState
import org.kiteio.punica.ui.dp4

/**
 * 文本输入框
 * @param value
 * @param onValueChange
 * @param modifier
 * @param enabled
 * @param readOnly
 * @param label
 * @param placeholder
 * @param leadingIcon
 * @param trailingIcon
 * @param visualTransformation
 * @param keyboardType 软键盘类型，默认为文本
 * @param imeAction 软键盘回车键行为，默认为下一步
 * @param keyboardActions
 * @param singleLine
 * @param maxLines
 * @param minLines
 */
@Composable
fun TextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    keyboardActions: KeyboardActions = KeyboardActions(imeAction, LocalFocusManager.current),
    singleLine: Boolean = true,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1
) {
    var isFocused by remember { mutableStateOf(false) }

    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .run { if (this == Modifier) fillMaxWidth() else this }
            .onFocusChanged { isFocused = it.isFocused },
        enabled = enabled,
        readOnly = readOnly,
        label = label?.run {
            {
                Column {
                    label()
                    AnimatedVisibility(visible = isFocused) {
                        Spacer(modifier = Modifier.height(dp4(2)))
                    }
                }
            }
        },
        placeholder = placeholder,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        visualTransformation = visualTransformation,
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = imeAction
        ),
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        maxLines = maxLines,
        minLines = minLines,
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = Color.Transparent,
            focusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            errorContainerColor = Color.Transparent
        )
    )
}


/**
 * 密码输入框
 * @param value
 * @param onValueChange
 * @param modifier
 * @param enabled
 * @param readOnly
 * @param label
 * @param placeholder
 * @param leadingIcon
 * @param keyboardType 软键盘类型，默认为文本
 * @param imeAction 软键盘回车键行为，默认为下一步
 * @param keyboardActions
 * @param singleLine
 * @param maxLines
 * @param minLines
 */
@Composable
fun PasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    keyboardActions: KeyboardActions = KeyboardActions(imeAction, LocalFocusManager.current),
    singleLine: Boolean = true,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1
) {
    var valueVisible by remember { mutableStateOf(false) }

    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        enabled = enabled,
        readOnly = readOnly,
        label = label,
        placeholder = placeholder,
        leadingIcon = leadingIcon,
        trailingIcon = {
            val icon = with(Icons.Rounded) { if (valueVisible) Visibility else VisibilityOff }
            IconButton(onClick = { valueVisible = !valueVisible }, enabled = enabled) {
                Icon(imageVector = icon)
            }
        },
        visualTransformation = if (valueVisible) VisualTransformation.None
        else PasswordVisualTransformation(),
        keyboardType = keyboardType,
        imeAction = imeAction,
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        maxLines = maxLines,
        minLines = minLines
    )
}


/**
 * 搜索栏
 * @param value
 * @param onValueChange
 * @param modifier
 * @param onSearch
 * @param placeholder
 * @param leadingIcon
 * @param trailingIcon
 */
@Composable
fun SearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    onSearch: () -> Unit = {},
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = {
        IconButton(onClick = onSearch) {
            Icon(
                imageVector = Icons.Rounded.Search,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    },
    trailingIcon: @Composable (() -> Unit)? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val interactions by interactionSource.interactions.collectAsState()

    val clickInteractionSource = remember { MutableInteractionSource() }

    // 监听 interactions 并发送给点击的 clickInteractionSource
    LaunchedEffect(key1 = interactions) {
        interactions?.let { clickInteractionSource.tryEmit(it) }
    }

    Surface(
        modifier = modifier.wrapContentSize(),
        shape = MaterialTheme.shapes.extraLarge,
        shadowElevation = 1.dp
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.extraLarge)
                .clickable(
                    interactionSource = clickInteractionSource,
                    indication = LocalIndication.current
                ) {},
            placeholder = placeholder,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            keyboardActions = KeyboardActions(
                ImeAction.Search,
                LocalFocusManager.current,
                onSearch = { onSearch() }
            ),
            interactionSource = interactionSource,
            shape = MaterialTheme.shapes.extraLarge,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = MaterialTheme.colorScheme.primary.copy(0.4f),
                focusedBorderColor = MaterialTheme.colorScheme.primary.copy(0.8f)
            )
        )
    }
}


/**
 * 根据 [ImeAction] 生成 [KeyboardActions]
 * 如果提供 [FocusManager]，在 [KeyboardActions.onDone] 会清除焦点
 * 在 [KeyboardActions.onNext] 会将焦点移动到 [FocusDirection.Next]
 * @param imeAction
 * @param focusManager
 * @param onGo
 * @param onPrevious
 * @param onSearch
 * @param onSend
 * @return [KeyboardActions]
 */
private fun KeyboardActions(
    imeAction: ImeAction,
    focusManager: FocusManager?,
    onGo: (KeyboardActionScope.() -> Unit)? = null,
    onPrevious: (KeyboardActionScope.() -> Unit)? = null,
    onSearch: (KeyboardActionScope.() -> Unit)? = null,
    onSend: (KeyboardActionScope.() -> Unit)? = null
) = KeyboardActions(
    onDone = if (imeAction == ImeAction.Done) ({ focusManager?.clearFocus() }) else null,
    onNext = if (imeAction == ImeAction.Done) ({ focusManager?.moveFocus(FocusDirection.Next) }) else null,
    onGo = onGo,
    onPrevious = onPrevious,
    onSearch = onSearch,
    onSend = onSend
)