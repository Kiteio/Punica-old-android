package org.kiteio.punica.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * 元素尺寸规范
 * @param multiplier 倍数
 * @return [Dp]
 */
@Composable
fun dp4(multiplier: Int = 1) = remember { 4.dp * multiplier }