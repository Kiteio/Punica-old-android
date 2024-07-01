package org.kiteio.punica.ui.component

import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * [Icon]
 * @param imageVector
 * @param modifier
 * @param tint
 */
@Composable
fun Icon(
    imageVector: ImageVector,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current
) {
    Icon(
        imageVector = imageVector,
        contentDescription = imageVector.name,
        modifier = modifier,
        tint = tint
    )
}