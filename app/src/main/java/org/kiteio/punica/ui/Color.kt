package org.kiteio.punica.ui

import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
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


/** 链接颜色 */
val Color.Companion.Link get() = Color(0, 109, 204)


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


/**
 * 弱化 [LocalContentColor]
 * @param alpha
 * @return [Color]
 */
@Composable
fun subduedContentColor(alpha: Float = 0.7f) = LocalContentColor.current.copy(alpha).applyLocalAlpha()


/**
 * 遵循 [LocalContentColor] 的 alpha 值
 * @receiver [Color]
 * @return [Color]
 */
@Composable
fun Color.applyLocalAlpha() = LocalContentColor.current.alpha.let { alpha ->
    if (alpha != 1f) copy(alpha) else this
}