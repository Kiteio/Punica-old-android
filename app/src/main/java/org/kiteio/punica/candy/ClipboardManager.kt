package org.kiteio.punica.candy

import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.text.buildAnnotatedString

/**
 * 向剪贴板设置文字
 * @receiver [ClipboardManager]
 * @param text
 */
fun ClipboardManager.setText(text: String) = setText(buildAnnotatedString { append(text) })