package org.kiteio.punica.ui

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import kotlin.random.Random

fun Color.Companion.random() = Color(
    Random.nextInt(0, 255),
    Random.nextInt(0, 255),
    Random.nextInt(0, 255),
    Random.nextInt(0, 255)
)


@OptIn(ExperimentalStdlibApi::class)
fun Color.toHexString() = toArgb().toHexString(HexFormat.UpperCase)


@OptIn(ExperimentalStdlibApi::class)
fun String.toColor() = Color(hexToInt(HexFormat.UpperCase))