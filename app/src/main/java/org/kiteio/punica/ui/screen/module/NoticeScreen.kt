package org.kiteio.punica.ui.screen.module

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.OpenInNew
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.paging.compose.collectAsLazyPagingItems
import com.rajat.pdfviewer.compose.PdfRendererViewCompose
import dev.jeziellago.compose.markdowntext.MarkdownText
import org.kiteio.punica.candy.launchCatching
import org.kiteio.punica.edu.EduNotice
import org.kiteio.punica.edu.Notice
import org.kiteio.punica.edu.NoticeItem
import org.kiteio.punica.openUri
import org.kiteio.punica.ui.component.BottomSheet
import org.kiteio.punica.ui.component.Icon
import org.kiteio.punica.ui.component.LazyPagingColumn
import org.kiteio.punica.ui.component.NavBackTopAppBar
import org.kiteio.punica.ui.component.Pager
import org.kiteio.punica.ui.component.PagingSource
import org.kiteio.punica.ui.component.ScaffoldBox
import org.kiteio.punica.ui.component.SubduedText
import org.kiteio.punica.ui.component.Title
import org.kiteio.punica.ui.component.items
import org.kiteio.punica.ui.dp4
import org.kiteio.punica.ui.Link
import org.kiteio.punica.ui.navigation.Route

/**
 * 教学通知
 */
@Composable
fun NoticeScreen() {
    val context = LocalContext.current

    // Pager 在切换页面时不能保留已加载内容，并且目前找不到解决方案
    val pager = remember { Pager(14) { NoticePagingSource() } }
    val noticeItems = pager.flow.collectAsLazyPagingItems()

    var noticeBottomSheetVisible by remember { mutableStateOf(false) }
    var visibleNoticeItem by remember { mutableStateOf<NoticeItem?>(null) }

    ScaffoldBox(topBar = { NavBackTopAppBar(route = Route.Module.Notice) }) {
        LazyPagingColumn(
            loadState = noticeItems.loadState,
            contentPadding = PaddingValues(dp4(2))
        ) {
            items(noticeItems) {
                ElevatedCard(modifier = Modifier.padding(dp4(2))) {
                    Surface(
                        onClick = {
                            visibleNoticeItem = it
                            noticeBottomSheetVisible = true
                        }
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(dp4(2))
                        ) {
                            Title(text = it.title)
                            Spacer(modifier = Modifier.height(dp4()))
                            SubduedText(text = it.time)
                        }
                    }
                    HorizontalDivider(modifier = Modifier.padding(horizontal = dp4()))
                    Surface {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(dp4(2)),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            SubduedText(text = it.url, modifier = Modifier.weight(1f))
                            IconButton(onClick = { context.openUri(it.url) }) {
                                Icon(imageVector = Icons.AutoMirrored.Rounded.OpenInNew)
                            }
                        }
                    }
                }
            }
        }
    }

    NoticeBottomSheet(
        visible = noticeBottomSheetVisible,
        onDismiss = { noticeBottomSheetVisible = false },
        noticeItem = visibleNoticeItem
    )
}


/**
 * 教学通知 [PagingSource]
 */
private class NoticePagingSource : PagingSource<NoticeItem>() {
    override suspend fun loadCatching(params: LoadParams<Int>) =
        Page(EduNotice.list(params.key!!), params)
}


/**
 * 教学通知详情
 * @param visible
 * @param onDismiss
 * @param noticeItem
 */
@Composable
private fun NoticeBottomSheet(visible: Boolean, onDismiss: () -> Unit, noticeItem: NoticeItem?) {
    BottomSheet(visible = visible, onDismiss = onDismiss, skipPartiallyExpanded = true) {
        var notice by remember { mutableStateOf<Notice?>(null) }

        LaunchedEffect(key1 = Unit) {
            launchCatching {
                noticeItem?.let { notice = EduNotice.notice(it) }
            }
        }

        notice?.run {
            if (pdf != null) {
                PdfRendererViewCompose(url = pdf)
            } else if (markdown != null) {
                LazyColumn(contentPadding = PaddingValues(dp4(4))) {
                    item {
                        MarkdownText(
                            markdown = markdown,
                            linkColor = Color.Link,
                            isTextSelectable = true
                        )
                    }
                }
            }
        }
    }
}