package org.kiteio.punica.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerScope
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.kiteio.punica.ui.dp4

/**
 * [PrimaryTabRow] 与 [HorizontalPager] 的有机结合
 * @param state
 * @param tabContent
 * @param pageContent
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> TabPager(
    state: TabPagerState<T>,
    tabContent: @Composable (tab: T) -> Unit,
    pageContent: @Composable PagerScope.(page: Int) -> Unit
) = with(state) {
    Column {
        Surface(shadowElevation = 0.8.dp) {
            if (state.pageCount <= 5) {
                PrimaryTabRow(
                    selectedTabIndex = currentPage,
                    divider = {}
                ) {
                    Tabs(tabContent = tabContent)
                }
            } else {
                PrimaryScrollableTabRow(selectedTabIndex = currentPage, divider = {}) {
                    Tabs(tabContent = tabContent)
                }
            }
        }
        org.kiteio.punica.ui.component.HorizontalPager(
            state = state,
            pageContent = { Box(modifier = Modifier.fillMaxSize()) { pageContent(it) } }
        )
    }
}


/**
 * [Tab]s
 * @receiver [TabPagerState]<[T]>
 * @param tabContent
 */
@Composable
private fun <T> TabPagerState<T>.Tabs(tabContent: @Composable (tab: T) -> Unit) {
    val coroutineScope = rememberCoroutineScope()

    tabs.forEachIndexed { index, tab ->
        Tab(
            selected = index == currentPage,
            onClick = { coroutineScope.launch { animateScrollToPage(index) } },
        ) {
            Box(modifier = Modifier.padding(vertical = dp4(2))) { tabContent(tab) }
        }
    }
}


/**
 * [TabPager] 状态
 * @param T
 * @property tabs
 * @property pageCount
 */
class TabPagerState<T>(
    initialPage: Int,
    val tabs: List<T>
) : PagerState(initialPage) {
    override val pageCount = tabs.size
}


/**
 * 返回 [TabPagerState]
 * @param tab
 * @param initialPage
 * @return [TabPagerState]<[T]>
 */
@Composable
inline fun <reified T : Any> rememberTabPagerState(
    vararg tab: T,
    initialPage: Int = 0
): TabPagerState<T> = rememberSaveable(
    saver = listSaver(
        save = { listOf(it.currentPage, it.tabs) },
        restore = { TabPagerState(it[0] as Int, (it[1] as List<*>).filterIsInstance<T>()) }
    )
) { TabPagerState(initialPage, tab.toList()) }


/**
 * [HorizontalPager]
 * @param state
 * @param modifier
 * @param contentPadding
 * @param pageContent
 */
@Composable
fun HorizontalPager(
    state: PagerState,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    pageContent: @Composable PagerScope.(page: Int) -> Unit
) {
    HorizontalPager(
        state = state,
        modifier = modifier,
        contentPadding = contentPadding,
        beyondViewportPageCount = 1,
        pageContent = pageContent
    )
}