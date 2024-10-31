package org.kiteio.punica.ui

import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.core.graphics.drawable.toBitmap
import androidx.palette.graphics.Palette
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.materialkolor.PaletteStyle
import com.materialkolor.rememberDynamicColorScheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.kiteio.punica.R
import org.kiteio.punica.candy.catchUnit
import org.kiteio.punica.candy.collectAsState
import org.kiteio.punica.datastore.Keys
import org.kiteio.punica.datastore.Preferences
import org.kiteio.punica.ui.component.Image

lateinit var avatarPainter: AsyncImagePainter
    private set
var avatarImageBitmap by mutableStateOf<Bitmap?>(null)
    private set

/**
 * 小石榴主题
 * @param darkTheme 是否为暗色
 * @param content
 */
@Composable
fun PunicaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val preferences by Preferences.data.collectAsState()
    val avatarUri by remember {
        derivedStateOf { preferences?.get(Keys.avatarUri)?.let { Uri.parse(it) } }
    }
    avatarPainter = rememberAsyncImagePainter(
        model = avatarUri,
        error = painterResource(id = R.drawable.punica),
        onSuccess = {
            avatarImageBitmap = it.result.drawable.toBitmap()
        }
    )
    // Painter 得使用才会请求图片
    Image(painter = avatarPainter, modifier = Modifier.alpha(0f))

    val themeColorSource by remember { derivedStateOf { preferences?.get(Keys.themeColorSource) ?: 0 } }

    val colorScheme = when {
        themeColorSource == 1 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        else -> {
            val themeStyle by remember {
                derivedStateOf {
                    val ordinal = preferences?.get(Keys.themeStyle) ?: 0
                    (PaletteStyle.entries.firstOrNull { it.ordinal == ordinal }
                        ?: PaletteStyle.TonalSpot)
                }
            }

            val randomColor = remember { Color.random() }

            val seedColor = when (themeColorSource) {
                0 -> rememberBitmapTheme(bitmap = avatarImageBitmap, randomColor)
                2 -> randomColor

                else -> {
                    val themeColor by remember {
                        derivedStateOf {
                            preferences?.get(Keys.themeColor)?.toColor() ?: randomColor
                        }
                    }
                    themeColor
                }
            }

            rememberDynamicColorScheme(
                primary = seedColor,
                isDark = darkTheme,
                isAmoled = false,
                style = themeStyle
            )
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}


/**
 * 获取 [bitmap] 主色
 * @param bitmap
 * @param init
 * @return [Color]
 */
@Stable
@Composable
private fun rememberBitmapTheme(
    bitmap: Bitmap?,
    init: Color
): Color {
    var themeColor by remember { mutableStateOf(init) }
    LaunchedEffect(bitmap) {
        bitmap?.let {
            catchUnit {
                withContext(Dispatchers.Default) {
                    Palette.from(bitmap.copy(Bitmap.Config.ARGB_8888, true))
                        .generate().dominantSwatch?.also { themeColor = Color(it.rgb) }
                }
            }
        }
    }

    return themeColor
}