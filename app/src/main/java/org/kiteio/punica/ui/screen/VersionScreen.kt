package org.kiteio.punica.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.QrCode2
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import io.github.alexzhirkevich.qrose.options.QrBallShape
import io.github.alexzhirkevich.qrose.options.QrBrush
import io.github.alexzhirkevich.qrose.options.QrFrameShape
import io.github.alexzhirkevich.qrose.options.QrLogoPadding
import io.github.alexzhirkevich.qrose.options.QrLogoShape
import io.github.alexzhirkevich.qrose.options.QrPixelShape
import io.github.alexzhirkevich.qrose.options.brush
import io.github.alexzhirkevich.qrose.options.circle
import io.github.alexzhirkevich.qrose.options.roundCorners
import io.github.alexzhirkevich.qrose.options.solid
import io.github.alexzhirkevich.qrose.rememberQrCodePainter
import io.ktor.client.statement.bodyAsText
import org.kiteio.punica.R
import org.kiteio.punica.candy.API
import org.kiteio.punica.candy.URLs
import org.kiteio.punica.candy.format
import org.kiteio.punica.candy.jsonArray
import org.kiteio.punica.candy.route
import org.kiteio.punica.getString
import org.kiteio.punica.openUri
import org.kiteio.punica.packageInfo
import org.kiteio.punica.request.fetch
import org.kiteio.punica.ui.component.Dialog
import org.kiteio.punica.ui.component.DialogVisibility
import org.kiteio.punica.ui.component.Icon
import org.kiteio.punica.ui.component.Image
import org.kiteio.punica.ui.component.NavBackTopAppBar
import org.kiteio.punica.ui.component.ScaffoldColumn
import org.kiteio.punica.ui.component.SubduedText
import org.kiteio.punica.ui.component.Title
import org.kiteio.punica.ui.dp4
import org.kiteio.punica.ui.navigation.Route
import org.kiteio.punica.ui.rememberRemoteList
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * 版本
 */
@Composable
fun VersionScreen() {
    val releases = rememberRemoteList { Gitee.releases() }
    val versionName = remember { packageInfo().versionName }
    val currentRelease by remember {
        derivedStateOf { releases.firstOrNull { it.name == versionName } }
    }

    var releaseDescDialogVisible by remember { mutableStateOf(false) }
    var releaseQRCodeDialogVisible by remember { mutableStateOf(false) }
    var visibleRelease by remember { mutableStateOf<Release?>(null) }

    ScaffoldColumn(
        topBar = { NavBackTopAppBar(route = Route.Version, shadowElevation = 0.dp) }
    ) {
        Surface(shadowElevation = 1.dp) {
            ElevatedCard(
                onClick = { visibleRelease = currentRelease; releaseDescDialogVisible = true },
                modifier = Modifier.padding(dp4(4))
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.punica),
                        modifier = Modifier.size(dp4(16))
                    )

                    Column(modifier = Modifier.padding(dp4(2))) {
                        Title(text = getString(R.string.app_name))
                        SubduedText(text = versionName)
                    }
                }
            }
        }

        LazyColumn(contentPadding = PaddingValues(dp4(2))) {
            items(releases) {
                ElevatedCard(
                    onClick = { visibleRelease = it; releaseDescDialogVisible = true },
                    modifier = Modifier.padding(dp4(2))
                ) {
                    Row(
                        modifier = Modifier.padding(dp4(4)),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Title(
                                text = it.name + if (it.name == versionName)
                                    getString(R.string.tip_current) else ""
                            )
                            SubduedText(text = it.time.format())
                            SubduedText(text = it.desc, maxLines = 3)
                        }
                        Spacer(modifier = Modifier.width(dp4(2)))

                        IconButton(
                            onClick = { visibleRelease = it; releaseQRCodeDialogVisible = true }
                        ) {
                            Icon(imageVector = Icons.Rounded.QrCode2)
                        }
                        IconButton(onClick = { openUri(it.url) }) {
                            Icon(imageVector = Icons.Rounded.Download)
                        }
                    }
                }
            }
        }
    }

    ReleaseDescDialog(
        visible = releaseDescDialogVisible,
        onDismiss = { releaseDescDialogVisible = false },
        release = visibleRelease
    )

    ReleaseQRCodeDialog(
        visible = releaseQRCodeDialogVisible,
        onDismiss = { releaseQRCodeDialogVisible = false },
        release = visibleRelease
    )
}


/**
 * 版本说明
 * @param visible
 * @param onDismiss
 * @param release
 */
@Composable
private fun ReleaseDescDialog(visible: Boolean, onDismiss: () -> Unit, release: Release?) {
    DialogVisibility(visible = visible) {
        release?.run {
            Dialog(
                title = { Text(text = name) },
                text = { Text(text = desc) },
                onDismiss = onDismiss,
                contentHorizontalAlignment = Alignment.Start
            )
        }
    }
}


/**
 * 发行版二维码
 */
@Composable
private fun ReleaseQRCodeDialog(visible: Boolean, onDismiss: () -> Unit, release: Release?) {
    DialogVisibility(visible = visible) {
        release?.run {
            val logo = painterResource(id = R.drawable.punica)
            val colorScheme = MaterialTheme.colorScheme
            val painter = rememberQrCodePainter(url) {
                logo {
                    painter = logo
                    padding = QrLogoPadding.Natural(0.1f)
                    shape = QrLogoShape.circle()
                    size = 0.25f
                }
                shapes {
                    ball = QrBallShape.circle()
                    darkPixel = QrPixelShape.roundCorners()
                    frame = QrFrameShape.roundCorners(0.25f)
                }
                colors {
                    dark = QrBrush.brush {
                        Brush.linearGradient(
                            0f to colorScheme.primary,
                            1f to colorScheme.primary,
                            end = Offset(it, it)
                        )
                    }
                    frame = QrBrush.solid(colorScheme.primary)
                }
            }

            Dialog(
                text = {
                    Title(text = getString(R.string.punica_together, name))
                    Spacer(modifier = Modifier.height(dp4(4)))
                    Image(painter = painter, modifier = Modifier.size(160.dp))
                },
                onDismiss = onDismiss
            )
        }
    }
}


/**
 * Gitee API
 */
private object Gitee : API {
    override val root = URLs.GITEE

    /** 发行版列表 */
    private const val RELEASES = "/api/v5/repos/${URLs.PUNICA_ROUTE}/releases"


    /**
     * 发行版列表
     * @return [List]<[Release]>
     */
    suspend fun releases(): List<Release> {
        val jsonArray = fetch(route { RELEASES }).bodyAsText().jsonArray
        val releases = arrayListOf<Release>()

        for (index in 0..<jsonArray.length()) {
            val json = jsonArray.getJSONObject(index)

            val url = json.getJSONArray("assets").run {
                for (i in 0..length()) {
                    val item = getJSONObject(i)

                    if (item.getString("name").endsWith(".apk")) {
                        return@run item.getString("browser_download_url")
                    }
                }
                null
            }

            if (url == null) continue

            releases.add(
                Release(
                    json.getString("tag_name"),
                    LocalDateTime.parse(
                        json.getString("created_at"),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX")
                    ),
                    json.getString("body"),
                    url
                )
            )
        }

        return releases
    }
}


/**
 * 发行版本
 * @property name 版本名称
 * @property time 发布时间
 * @property desc 更新说明
 * @property url 下载地址
 */
private data class Release(
    val name: String,
    val time: LocalDateTime,
    val desc: String,
    val url: String
)