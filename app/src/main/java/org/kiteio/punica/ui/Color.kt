package org.kiteio.punica.ui

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import kotlin.random.Random

/**
 * 随机颜色
 * @receiver [Color.Companion]
 * @return [Color]
 */
fun Color.Companion.random() = Color(
    Random.nextInt(0, 255),
    Random.nextInt(0, 255),
    Random.nextInt(0, 255),
    Random.nextInt(0, 255)
)


/**
 * 颜色转十六进制
 * @receiver [Color]
 * @return [String]
 */
@OptIn(ExperimentalStdlibApi::class)
fun Color.toHexString() = toArgb().toHexString(HexFormat.UpperCase)


/**
 * 十六进制转颜色
 * @receiver [String]
 * @return [Color]
 */
@OptIn(ExperimentalStdlibApi::class)
fun String.toColor() = Color(hexToInt(HexFormat.UpperCase))