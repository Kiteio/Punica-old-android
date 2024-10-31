package org.kiteio.punica.ui.screen.bottom

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowColumn
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Timeline
import androidx.compose.material.icons.rounded.ViewWeek
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import org.kiteio.punica.R
import org.kiteio.punica.Toast
import org.kiteio.punica.candy.LocalDateNow
import org.kiteio.punica.candy.collectAsState
import org.kiteio.punica.candy.daysUntil
import org.kiteio.punica.candy.launchCatch
import org.kiteio.punica.datastore.Keys
import org.kiteio.punica.datastore.Preferences
import org.kiteio.punica.datastore.Timetables
import org.kiteio.punica.edu.foundation.Campus
import org.kiteio.punica.edu.foundation.Schedule
import org.kiteio.punica.edu.foundation.Semester
import org.kiteio.punica.edu.system.EduSystem
import org.kiteio.punica.edu.system.api.Timetable
import org.kiteio.punica.edu.system.api.TimetableItem
import org.kiteio.punica.edu.system.api.timetable
import org.kiteio.punica.getString
import org.kiteio.punica.getStringArray
import org.kiteio.punica.ui.collectAsEduSystemIdentified
import org.kiteio.punica.ui.component.CheckBox
import org.kiteio.punica.ui.component.Dialog
import org.kiteio.punica.ui.component.DialogVisibility
import org.kiteio.punica.ui.component.DropdownMenuItem
import org.kiteio.punica.ui.component.HorizontalPager
import org.kiteio.punica.ui.component.Icon
import org.kiteio.punica.ui.component.IconText
import org.kiteio.punica.ui.component.ScaffoldColumn
import org.kiteio.punica.ui.component.SubduedText
import org.kiteio.punica.ui.component.Title
import org.kiteio.punica.ui.component.TopAppBar
import org.kiteio.punica.ui.dp4
import org.kiteio.punica.ui.rememberLastUsername
import org.kiteio.punica.ui.subduedContentColor
import java.time.LocalDate

/**
 * 日程
 */
@Composable
fun ScheduleScreen() {
    var semester by remember { mutableStateOf(EduSystem.semester) }
    val timetable = Timetables.collectAsEduSystemIdentified(
        id = rememberLastUsername(semester),
        key = semester
    ) { timetable(semester) }

    val preferences by Preferences.data.collectAsState()

    val schoolStart by remember {
        derivedStateOf { preferences?.get(Keys.schoolStart)?.let { LocalDate.parse(it) } }
    }
    val week by remember {
        derivedStateOf {
            schoolStart?.run {
                (daysUntil(LocalDateNow) + dayOfWeek.value - LocalDateNow.dayOfWeek.value)
                    .toInt() / 7 + 1
            } ?: 0
        }
    }

    val pagerState = rememberPagerState(initialPage = 30) { 60 }
    var itemsDialogVisible by remember { mutableStateOf(false) }
    var visibleItems by remember { mutableStateOf<List<TimetableItem>?>(null) }

    ScaffoldColumn(
        topBar = {
            TopAppBar(
                pagerState = pagerState,
                preferences = preferences,
                week = week,
                semester = semester,
                onSemesterChange = { semester = it }
            )
        }
    ) {
        Timetable(
            pagerState = pagerState,
            week = week,
            semester = semester,
            timetable = timetable,
            onItemClick = { visibleItems = it; itemsDialogVisible = true },
            showOtherWeeks = preferences?.get(Keys.showOtherWeeks) ?: false,
            modifier = Modifier.weight(1f)
        )
        // 课表备注
        timetable?.apply {
            if (remark != "未安排时间课程：") PlaidText(
                text = remark,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Start
            )
        }
    }

    ItemsDialog(
        visible = itemsDialogVisible,
        onDismiss = { itemsDialogVisible = false },
        items = visibleItems
    )
}


/**
 * 导航栏
 * @param pagerState
 * @param preferences
 * @param week 当前周次
 * @param semester 选中学期
 * @param onSemesterChange 学期选择事件
 */
@Composable
private fun TopAppBar(
    pagerState: PagerState,
    preferences: Preferences?,
    week: Int,
    semester: Semester,
    onSemesterChange: (Semester) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var semesterDropdownMenuExpanded by remember { mutableStateOf(false) }
    var moreDropdownMenuExpanded by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            Text(
                text = getString(R.string.week_count, week),
                style = MaterialTheme.typography.bodyLarge
            )
        },
        shadowElevation = 0.dp,
        actions = {
            TextButton(onClick = { semesterDropdownMenuExpanded = true }) {
                Text(text = "$semester")
                preferences?.get(Keys.lastUsername)?.let { username ->
                    DropdownMenu(
                        expanded = semesterDropdownMenuExpanded,
                        onDismissRequest = { semesterDropdownMenuExpanded = false }
                    ) {
                        val firstHalf = getString(R.string.first_half)
                        val lastHalf = getString(R.string.last_half)
                        val grades = getStringArray(R.array.grades).flatMap {
                            listOf(it + firstHalf, it + lastHalf)
                        }

                        val semesters = remember(username) {
                            mutableStateListOf<Semester>().apply {
                                addAll(Semester.listFor(username))
                            }
                        }

                        semesters.forEachIndexed { index, item ->
                            DropdownMenuItem(
                                text = { Text(text = "${grades.getOrElse(index) { "" }} $item") },
                                onClick = {
                                    onSemesterChange(item)
                                    semesterDropdownMenuExpanded = false
                                },
                                selected = item == semester,
                            )
                        }
                        DropdownMenuItem(
                            text = { Text(text = getString(R.string.more)) },
                            onClick = {
                                semesters.add(semesters.last() + 1)
                            }
                        )
                    }
                }
            }

            // 更多按钮
            IconButton(onClick = { moreDropdownMenuExpanded = true }) {
                Icon(imageVector = Icons.Rounded.MoreVert)
                DropdownMenu(
                    expanded = moreDropdownMenuExpanded,
                    onDismissRequest = { moreDropdownMenuExpanded = false }) {
                    DropdownMenuItem(
                        text = { Text(text = getString(R.string.back_to_this_week)) },
                        onClick = {
                            coroutineScope.launchCatch {
                                onSemesterChange(EduSystem.semester)
                                pagerState.scrollToPage(pagerState.pageCount / 2)
                                moreDropdownMenuExpanded = false
                            }
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(text = getString(R.string.show_other_weeks)) },
                        onClick = {
                            coroutineScope.launchCatch {
                                Preferences.edit {
                                    it[Keys.showOtherWeeks] = !(it[Keys.showOtherWeeks] ?: false)
                                }
                                moreDropdownMenuExpanded = false
                            }
                        },
                        trailingIcon = {
                            CheckBox(
                                checked = preferences?.get(Keys.showOtherWeeks) ?: false,
                                onCheckedChange = { value ->
                                    coroutineScope.launchCatch {
                                        Preferences.edit { it[Keys.showOtherWeeks] = value }
                                        moreDropdownMenuExpanded = false
                                    }
                                }
                            )
                        }
                    )
                }
            }
        }
    )
}


/**
 * 课表
 * @param pagerState
 * @param week 当前周次
 * @param semester
 * @param timetable
 * @param onItemClick
 * @param showOtherWeeks
 * @param modifier
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun Timetable(
    pagerState: PagerState,
    week: Int,
    semester: Semester,
    timetable: Timetable?,
    onItemClick: (List<TimetableItem>?) -> Unit,
    showOtherWeeks: Boolean,
    modifier: Modifier = Modifier
) {
    val height = dp4(14)
    val timeBarWidth = dp4(10)
    val daysOfWeek = getStringArray(R.array.days_of_week)

    HorizontalPager(state = pagerState, modifier = modifier) { page ->
        val offset = page - (pagerState.pageCount / 2)
        val offsetWeek = week + offset
        val offsetNow =
            if (semester != EduSystem.semester) null else LocalDateNow.plusWeeks(offset.toLong())

        LazyColumn {
            // 顶部周次和星期几
            stickyHeader {
                Surface(shadowElevation = 0.8.dp) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .height(dp4(12))
                            .background(MaterialTheme.colorScheme.surface)
                    ) {
                        Box(
                            modifier = Modifier
                                .width(timeBarWidth)
                                .fillMaxHeight(),
                            contentAlignment = Alignment.Center
                        ) {
                            Title(
                                text = getString(R.string.week_number, offsetWeek),
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                        daysOfWeek.forEachIndexed { index, item ->
                            val date = offsetNow?.plusDays(
                                index.toLong() + 1 - offsetNow.dayOfWeek.value
                            )

                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth(1f / (7 - index))
                                    .fillMaxHeight()
                                    .padding(1.dp),
                                shadowElevation = if (date == LocalDateNow) 2.dp else 0.dp,
                                shape = MaterialTheme.shapes.medium
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    CompositionLocalProvider(
                                        value = LocalContentColor provides
                                                if (date == LocalDateNow) MaterialTheme.colorScheme.primary
                                                else LocalContentColor.current.copy(0.3f)
                                    ) {
                                        Text(
                                            text = item,
                                            fontWeight = FontWeight.Black.takeIf { date == LocalDateNow }
                                        )
                                        AnimatedVisibility(visible = date != null) {
                                            if (date != null) PlaidText(
                                                text = "${date.month.value}-${date.dayOfMonth}"
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            item {
                Row {
                    TimeBar(width = timeBarWidth, itemHeight = height * 2)
                    FlowColumn(maxItemsInEachColumn = 6, modifier = Modifier.weight(1f)) {
                        var lastWeight = 0

                        timetable?.items?.forEach {
                            it.Plaid(
                                week = offsetWeek,
                                onClick = { onItemClick(it) },
                                lastWeight = lastWeight,
                                onLastWeightChange = { value -> lastWeight = value },
                                showOtherWeeks = showOtherWeeks,
                                height = height
                            )
                        }
                    }
                }
            }
        }
    }
}


/**
 * 左侧时间条
 * @param width
 * @param itemHeight
 */
@Composable
private fun TimeBar(width: Dp, itemHeight: Dp) {
    val coroutineScope = rememberCoroutineScope()
    val preferences by Preferences.data.collectAsState()
    val campusId by remember { derivedStateOf { preferences?.get(Keys.campusId) } }

    Column {
        remember(campusId) { Schedule.getById(campusId) }.items.forEachIndexed { index, section ->
            Surface(
                modifier = Modifier
                    .border(0.5.dp, MaterialTheme.colorScheme.surfaceVariant)
                    .clickable {
                        coroutineScope.launchCatch {
                            Preferences.edit {
                                it[Keys.campusId] =
                                    if (campusId == Campus.Canton.id) Campus.Foshan.id
                                    else Campus.Canton.id
                            }
                            Toast(
                                getString(
                                    R.string.schedule_change_to,
                                    getString(Campus.getById(campusId).nameResId)
                                )
                            ).show()
                        }
                    }
            ) {
                Column(
                    modifier = Modifier
                        .width(width)
                        .height(itemHeight),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CompositionLocalProvider(LocalContentColor provides subduedContentColor()) {
                        val fontSize = 10.sp

                        PlaidText(text = "${index * 2 + 1}-${index * 2 + 2}", fontSize = fontSize)
                        Spacer(modifier = Modifier.height(dp4()))
                        PlaidText(text = "${section.first.first}", fontSize = fontSize)
                        PlaidText(text = "${section.first.second}", fontSize = fontSize)
                        Spacer(modifier = Modifier.height(dp4()))
                        PlaidText(text = "${section.second.first}", fontSize = fontSize)
                        PlaidText(text = "${section.second.second}", fontSize = fontSize)
                    }
                }
            }
        }
    }
}


/**
 * 单个格子
 * @receiver [List]<[TimetableItem]>?
 * @param week 当前周次
 * @param onClick
 * @param lastWeight
 * @param onLastWeightChange
 * @param showOtherWeeks
 * @param height
 */
@Composable
private fun List<TimetableItem>?.Plaid(
    week: Int,
    onClick: () -> Unit,
    lastWeight: Int,
    onLastWeightChange: (Int) -> Unit,
    showOtherWeeks: Boolean,
    height: Dp
) {
    if (isNullOrEmpty()) {
        Blank(lastWeight = lastWeight, onLastWeightChange = onLastWeightChange, height = height)
    } else {
        val firstContainsWeek = firstOrNull { it.weeks.contains(week) }
        val alpha = if (firstContainsWeek != null) 1f else 0.35f

        (firstContainsWeek ?: if (showOtherWeeks) first() else null)?.apply {
            Surface(
                onClick = onClick,
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.secondaryContainer,
                border = BorderStroke(
                    0.6.dp,
                    MaterialTheme.colorScheme.outlineVariant
                ),
                modifier = Modifier.fillMaxWidth(1f / 7)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(height * section.size.also { onLastWeightChange(it) })
                        .padding(dp4() / 2)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        PlaidText(
                            text = name,
                            maxLines = 3,
                            color = MaterialTheme.colorScheme.primary.copy(alpha),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(dp4(2)))
                        PlaidText(
                            text = area.replace(Regex("[(（].*?[）)]"), ""),
                            maxLines = 3,
                            color = MaterialTheme.colorScheme.primary.copy(alpha),
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (size > 1) SubduedText(
                        text = "+${size - 1}",
                        modifier = Modifier.align(Alignment.BottomEnd),
                        color = subduedContentColor(if (firstContainsWeek != null) 0.4f else 0.2f),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp)
                    )
                }
            }
        } ?: Blank(
            lastWeight = lastWeight,
            onLastWeightChange = onLastWeightChange,
            height = height
        )
    }
}


@Composable
private fun Blank(lastWeight: Int, onLastWeightChange: (Int) -> Unit, height: Dp) {
    val weight = when (lastWeight) {
        1, 3 -> 1
        4 -> 0
        else -> 2
    }.also { onLastWeightChange(it) }

    Spacer(
        modifier = Modifier
            .fillMaxWidth(1f / 7)
            .height(height * weight)
    )
}


/**
 * 格子中的文字
 * @param text
 * @param color
 * @param fontSize
 * @param fontWeight
 * @param textAlign
 * @param maxLines
 */
@Composable
private fun PlaidText(
    text: String,
    color: Color = LocalContentColor.current,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontWeight: FontWeight? = null,
    textAlign: TextAlign = TextAlign.Center,
    maxLines: Int = Int.MAX_VALUE
) {
    Text(
        text = text,
        color = color,
        fontSize = fontSize,
        fontWeight = fontWeight,
        textAlign = textAlign,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis,
        style = MaterialTheme.typography.labelSmall
    )
}


/**
 * 课程信息
 * @param visible
 * @param onDismiss
 * @param items
 */
@Composable
private fun ItemsDialog(visible: Boolean, onDismiss: () -> Unit, items: List<TimetableItem>?) {
    DialogVisibility(visible = visible) {
        if (items != null) {
            Dialog(
                text = {
                    items.forEach {
                        Card(onClick = {}, modifier = Modifier.padding(dp4())) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(dp4(4))
                            ) {
                                Title(text = it.name)
                                Spacer(modifier = Modifier.height(dp4(2)))
                                IconText(
                                    text = it.teacher,
                                    leadingText = getString(R.string.teacher),
                                    leadingIcon = Icons.Rounded.Person
                                )
                                Spacer(modifier = Modifier.height(dp4()))
                                IconText(
                                    text = it.area,
                                    leadingText = getString(R.string.area),
                                    leadingIcon = Icons.Rounded.LocationOn
                                )
                                Spacer(modifier = Modifier.height(dp4()))
                                IconText(
                                    text = it.weeksStr,
                                    leadingText = getString(R.string.weeks),
                                    leadingIcon = Icons.Rounded.ViewWeek
                                )
                                Spacer(modifier = Modifier.height(dp4()))
                                IconText(
                                    text = it.section.joinToString("-"),
                                    leadingText = getString(R.string.section),
                                    leadingIcon = Icons.Rounded.Timeline
                                )
                            }
                        }
                    }
                },
                onDismiss = onDismiss
            )
        }
    }
}