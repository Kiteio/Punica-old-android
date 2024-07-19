package org.kiteio.punica.ui.screen.module

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Class
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Timeline
import androidx.compose.material.icons.rounded.ViewWeek
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.kiteio.punica.R
import org.kiteio.punica.datastore.TimetableAlls
import org.kiteio.punica.edu.system.EduSystem
import org.kiteio.punica.edu.system.api.TimetableAll
import org.kiteio.punica.edu.system.api.timetableAll
import org.kiteio.punica.getString
import org.kiteio.punica.getStringArray
import org.kiteio.punica.ui.LocalViewModel
import org.kiteio.punica.ui.collectAsIdentified
import org.kiteio.punica.ui.component.BottomSheet
import org.kiteio.punica.ui.component.DropdownMenuItem
import org.kiteio.punica.ui.component.IconText
import org.kiteio.punica.ui.component.NavBackTopAppBar
import org.kiteio.punica.ui.component.ScaffoldColumn
import org.kiteio.punica.ui.component.SearchBar
import org.kiteio.punica.ui.component.Title
import org.kiteio.punica.ui.dp4
import org.kiteio.punica.ui.navigation.Route

/**
 * 全校课表
 */
@Composable
fun TimetableAllScreen() {
    val eduSystem = LocalViewModel.current.eduSystem
    val timetableAll = TimetableAlls.collectAsIdentified(id = EduSystem.semester.toString()) {
        eduSystem?.timetableAll()
    }

    val daysOfWeek = getStringArray(R.array.days_of_week)
    val sections = listOf("1-2", "3-4", "5-6", "7-8", "9-10", "11-12")

    var query by remember { mutableStateOf("") }
    var week by remember { mutableIntStateOf(0) }
    var dayOfWeek by remember { mutableIntStateOf(0) }
    var section by remember { mutableIntStateOf(0) }

    var infoBottomSheetVisible by remember { mutableStateOf(false) }
    var visibleKey by remember { mutableStateOf<String?>(null) }

    var keys by remember { mutableStateOf<List<String>>(emptyList()) }
    var filteredKeys by remember { mutableStateOf(keys) }

    LaunchedEffect(key1 = timetableAll) {
        launch(Dispatchers.Default) {
            timetableAll?.run { keys = map.keys.sorted() }
        }
    }

    LaunchedEffect(key1 = query, key2 = week + dayOfWeek + section, key3 = keys) {
        launch(Dispatchers.Default) {
            timetableAll?.run {
                filteredKeys = keys.filter { key ->
                    val isKeyContainsQuery = key.contains(query)

                    map[key]?.any {
                        // 周次符合
                        (week == 0 || it.weeks.contains(week)) &&
                                // 星期符合
                                (dayOfWeek == 0 || it.dayOfWeek == dayOfWeek) &&
                                (section == 0 || it.section.contains((section - 1) * 2 + 1)) &&
                                // 包含 query
                                (isKeyContainsQuery || it.teacher.contains(query) ||
                                        it.area.contains(query) || it.clazz.contains(query))
                    } ?: false
                }
            }
        }
    }

    ScaffoldColumn(
        topBar = {
            NavBackTopAppBar(
                route = Route.Module.TimetableAll,
                shadowElevation = 0.dp,
                actions = {
                    DropdownTextButton(
                        text = {
                            Text(
                                text = if (week == 0) getString(R.string.weeks) else getString(
                                    R.string.week_count,
                                    week
                                )
                            )
                        },
                        items = 1..16,
                        onItemClick = { week = it },
                        itemContent = { Text(text = getString(R.string.week_number, it)) },
                        selectedIndex = week
                    )

                    DropdownTextButton(
                        text = {
                            Text(
                                text = getString(
                                    R.string.day_of_week,
                                    if (dayOfWeek == 0) "" else daysOfWeek[dayOfWeek - 1]
                                )
                            )
                        },
                        items = daysOfWeek.toList(),
                        onItemClick = { dayOfWeek = it },
                        itemContent = { Text(text = getString(R.string.day_of_week, it)) },
                        selectedIndex = dayOfWeek
                    )

                    DropdownTextButton(
                        text = {
                            Text(
                                text = if (section == 0) getString(R.string.section)
                                else sections[section - 1]
                            )
                        },
                        items = sections,
                        onItemClick = { section = it },
                        itemContent = { Text(text = it) },
                        selectedIndex = section
                    )
                }
            )
        }
    ) {
        Surface(shadowElevation = 1.dp) {
            SearchBar(
                value = query,
                onValueChange = { query = it },
                onSearch = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dp4(4)),
                placeholder = {
                    Text(
                        text = getString(
                            R.string.input,
                            buildString {
                                append(getString(R.string.course), "、")
                                append(getString(R.string.teacher), "、")
                                append(getString(R.string.area), "、")
                                append(getString(R.string.clazz))
                            }
                        )
                    )
                }
            )
        }

        timetableAll?.run {
            LazyColumn(contentPadding = PaddingValues(dp4(2))) {
                items(filteredKeys) { key ->
                    ElevatedCard(
                        onClick = { visibleKey = key; infoBottomSheetVisible = true },
                        modifier = Modifier.padding(dp4(2))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(dp4(2))
                        ) {
                            Title(text = key)
                            Spacer(modifier = Modifier.height(dp4()))
                            CompositionLocalProvider(
                                value = LocalTextStyle provides MaterialTheme.typography.bodySmall
                            ) {
                                val list = map[key] ?: emptyList()

                                IconText(
                                    text = list.map { it.teacher }.toSet().joinToString("、"),
                                    leadingIcon = Icons.Rounded.Person,
                                    leadingText = getString(R.string.teacher)
                                )
                                IconText(
                                    text = list.map { it.weeksStr }.toSet().joinToString("、"),
                                    leadingIcon = Icons.Rounded.DateRange,
                                    leadingText = getString(R.string.weeks)
                                )
                                IconText(
                                    text = list.map { it.dayOfWeek }.toSet()
                                        .joinToString("、") { daysOfWeek[it - 1] },
                                    leadingIcon = Icons.Rounded.DateRange,
                                    leadingText = getString(R.string.day_of_week, "")
                                )
                                IconText(
                                    text = list.map { it.section.joinToString("-") }.toSet()
                                        .sorted().joinToString("、"),
                                    leadingIcon = Icons.Rounded.DateRange,
                                    leadingText = getString(R.string.section)
                                )
                                IconText(
                                    text = list.map { it.area }.toSet().joinToString("、"),
                                    leadingIcon = Icons.Rounded.LocationOn,
                                    leadingText = getString(R.string.area)
                                )
                                IconText(
                                    text = list.map { it.clazz }.toSet().joinToString("、"),
                                    leadingIcon = Icons.Rounded.Class,
                                    leadingText = getString(R.string.clazz)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    InfoBottomSheet(
        visible = infoBottomSheetVisible,
        onDismiss = { infoBottomSheetVisible = false; visibleKey = null },
        key = visibleKey,
        timetableAll = timetableAll,
        daysOfWeek = daysOfWeek
    )
}


/**
 * [DropdownMenu] + [TextButton]
 * @param text
 * @param items
 * @param onItemClick
 * @param itemContent
 * @param selectedIndex
 */
@Composable
private fun <T> DropdownTextButton(
    text: @Composable () -> Unit,
    items: Iterable<T>,
    onItemClick: (Int) -> Unit,
    itemContent: @Composable (T) -> Unit,
    selectedIndex: Int,
) {
    var expanded by remember { mutableStateOf(false) }

    TextButton(onClick = { expanded = true }) {
        text()
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(
                text = { Text(text = getString(R.string.all)) },
                onClick = { onItemClick(0); expanded = false },
                selected = selectedIndex == 0
            )
            items.forEachIndexed { index, item ->
                DropdownMenuItem(
                    text = { itemContent(item) },
                    onClick = { onItemClick(index + 1); expanded = false },
                    selected = selectedIndex == index + 1
                )
            }
        }
    }
}


/**
 * 课程详情
 * @param visible
 * @param onDismiss
 * @param key
 * @param timetableAll
 * @param daysOfWeek
 */
@Composable
private fun InfoBottomSheet(
    visible: Boolean,
    onDismiss: () -> Unit,
    key: String?,
    timetableAll: TimetableAll?,
    daysOfWeek: Array<String>
) {
    BottomSheet(visible = visible, onDismiss = onDismiss, skipPartiallyExpanded = true) {
        LazyColumn(contentPadding = PaddingValues(dp4(2))) {
            key?.let { key -> timetableAll?.map?.get(key) }?.let { items ->
                item {
                    Title(text = key, modifier = Modifier.padding(dp4(2)))
                }

                items(items) {
                    ElevatedCard(onClick = {}, modifier = Modifier.padding(dp4(2))) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(dp4(4))
                        ) {
                            Title(text = it.clazz, style = MaterialTheme.typography.bodyMedium)
                            Spacer(modifier = Modifier.height(dp4()))

                            CompositionLocalProvider(
                                value = LocalTextStyle provides MaterialTheme.typography.bodySmall
                            ) {
                                IconText(
                                    text = it.teacher,
                                    leadingIcon = Icons.Rounded.Person,
                                    leadingText = getString(R.string.teacher)
                                )
                                IconText(
                                    text = it.area,
                                    leadingIcon = Icons.Rounded.LocationOn,
                                    leadingText = getString(R.string.area)
                                )
                                IconText(
                                    text = it.weeksStr,
                                    leadingIcon = Icons.Rounded.ViewWeek,
                                    leadingText = getString(R.string.weeks)
                                )
                                IconText(
                                    text = it.section.joinToString("-"),
                                    leadingIcon = Icons.Rounded.Timeline,
                                    leadingText = getString(R.string.section)
                                )
                                IconText(
                                    text = daysOfWeek[it.dayOfWeek - 1],
                                    leadingIcon = Icons.Rounded.DateRange,
                                    leadingText = getString(R.string.day_of_week, "")
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}