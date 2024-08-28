package org.kiteio.punica.ui.screen.module

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowColumn
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountBox
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Numbers
import androidx.compose.material.icons.rounded.PersonOutline
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Timelapse
import androidx.compose.material.icons.rounded.Token
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.Pager
import androidx.paging.compose.collectAsLazyPagingItems
import org.kiteio.punica.R
import org.kiteio.punica.Toast
import org.kiteio.punica.candy.catching
import org.kiteio.punica.candy.launchCatching
import org.kiteio.punica.edu.foundation.Campus
import org.kiteio.punica.edu.system.CourseSystem
import org.kiteio.punica.edu.system.api.Token
import org.kiteio.punica.edu.system.api.course.Course
import org.kiteio.punica.edu.system.api.course.MyCourse
import org.kiteio.punica.edu.system.api.course.Priority
import org.kiteio.punica.edu.system.api.course.SearchParams
import org.kiteio.punica.edu.system.api.course.Section
import org.kiteio.punica.edu.system.api.course.Sort
import org.kiteio.punica.edu.system.api.course.delete
import org.kiteio.punica.edu.system.api.course.exit
import org.kiteio.punica.edu.system.api.course.list
import org.kiteio.punica.edu.system.api.course.log
import org.kiteio.punica.edu.system.api.course.myCourses
import org.kiteio.punica.edu.system.api.course.overview
import org.kiteio.punica.edu.system.api.course.search
import org.kiteio.punica.edu.system.api.course.select
import org.kiteio.punica.edu.system.api.token
import org.kiteio.punica.getString
import org.kiteio.punica.getStringArray
import org.kiteio.punica.ui.LocalNavController
import org.kiteio.punica.ui.LocalViewModel
import org.kiteio.punica.ui.component.BottomSheet
import org.kiteio.punica.ui.component.CheckBox
import org.kiteio.punica.ui.component.Dialog
import org.kiteio.punica.ui.component.DialogVisibility
import org.kiteio.punica.ui.component.DropdownMenuItem
import org.kiteio.punica.ui.component.Icon
import org.kiteio.punica.ui.component.IconText
import org.kiteio.punica.ui.component.NavBackTopAppBar
import org.kiteio.punica.ui.component.Pager
import org.kiteio.punica.ui.component.PagingSource
import org.kiteio.punica.ui.component.RadioButton
import org.kiteio.punica.ui.component.ScaffoldBox
import org.kiteio.punica.ui.component.SubduedText
import org.kiteio.punica.ui.component.TabPager
import org.kiteio.punica.ui.component.TextField
import org.kiteio.punica.ui.component.Title
import org.kiteio.punica.ui.component.items
import org.kiteio.punica.ui.component.rememberTabPagerState
import org.kiteio.punica.ui.dp4
import org.kiteio.punica.ui.navigation.Route
import org.kiteio.punica.ui.rememberRemote
import org.kiteio.punica.ui.rememberRemoteList
import org.kiteio.punica.ui.runWithReLogin
import java.time.DayOfWeek

/**
 * 选课系统
 */
@Composable
fun CourseSystemScreen() {
    val coroutineScope = rememberCoroutineScope()
    val navController = LocalNavController.current
    val eduSystem = LocalViewModel.current.eduSystem
    var token by remember { mutableStateOf<Token?>(null) }
    var courseSystem by remember { mutableStateOf<CourseSystem?>(null) }

    var timetableBottomSheetVisible by remember { mutableStateOf(false) }
    var logsBottomSheetVisible by remember { mutableStateOf(false) }
    var infoDialogVisible by remember { mutableStateOf(false) }
    var tokenDialogVisible by remember { mutableStateOf(false) }

    var searchParamsPE by remember { mutableStateOf(SearchParams()) }
    var searchParamsGeneral by remember { mutableStateOf(SearchParams()) }
    var searchParamsCrossMajor by remember { mutableStateOf(SearchParams()) }
    var searchParamsCrossYear by remember { mutableStateOf(SearchParams()) }
    var searchParamsMajor by remember { mutableStateOf(SearchParams()) }
    var searchParamsBottomSheetVisible by remember { mutableStateOf(false) }
    var visibleSearchParams by remember { mutableStateOf<SearchParams?>(null) }

    LaunchedEffect(key1 = eduSystem, key2 = token) {
        eduSystem?.runWithReLogin {
            courseSystem = catching<CourseSystem> { CourseSystem.from(eduSystem) }

            if (courseSystem == null) token?.let { token ->
                courseSystem = catching<CourseSystem> { CourseSystem.from(eduSystem, token) }
            }
        }
    }

    BackHandler(enabled = courseSystem != null) {
        coroutineScope.launchCatching { courseSystem?.exit() }
        navController.popBackStack()
    }

    val tabPagerState = rememberTabPagerState(
        R.string.course_basic,
        R.string.course_optional,
        R.string.course_pe,
        R.string.course_general,
        R.string.course_cross_major,
        R.string.course_cross_year,
        R.string.course_major
    )

    ScaffoldBox(
        topBar = {
            NavBackTopAppBar(
                route = Route.Module.CourseSystem,
                actions = {
                    courseSystem?.run {
                        IconButton(onClick = { timetableBottomSheetVisible = true }) {
                            Icon(imageVector = Icons.Rounded.Bookmark)
                        }
                        IconButton(onClick = { logsBottomSheetVisible = true }) {
                            Icon(imageVector = Icons.Rounded.History)
                        }
                        IconButton(onClick = { infoDialogVisible = true }) {
                            Icon(imageVector = Icons.Rounded.Info)
                        }
                    }
                    IconButton(onClick = { tokenDialogVisible = true }) {
                        Icon(imageVector = Icons.Rounded.Token)
                    }
                },
                shadowElevation = 0.dp
            )
        },
        floatingActionButton = {
            if (tabPagerState.currentPage > 1) {
                FloatingActionButton(
                    onClick = {
                        visibleSearchParams = when (tabPagerState.currentPage) {
                            2 -> searchParamsPE
                            3 -> searchParamsGeneral
                            4 -> searchParamsCrossMajor
                            5 -> searchParamsCrossYear
                            6 -> searchParamsMajor
                            else -> error("Invalid page")
                        }
                        searchParamsBottomSheetVisible = true
                    }
                ) {
                    Icon(imageVector = Icons.Rounded.Search)
                }
            }
        }
    ) {
        TabPager(state = tabPagerState, tabContent = { Text(text = getString(it)) }) { page ->
            courseSystem?.let { courseSystem ->
                when (page) {
                    0 -> UnsearchableCourses(courseSystem = courseSystem, sort = Sort.Basic)

                    1 -> UnsearchableCourses(courseSystem = courseSystem, sort = Sort.Optional)

                    2 -> SearchableCourses(
                        courseSystem = courseSystem,
                        sort = Sort.General,
                        searchParams = searchParamsPE,
                        predicate = { it.department == "体育教学部" })

                    3 -> SearchableCourses(
                        courseSystem = courseSystem,
                        sort = Sort.General,
                        searchParams = searchParamsGeneral
                    )

                    4 -> SearchableCourses(
                        courseSystem = courseSystem,
                        sort = Sort.CrossMajor,
                        searchParams = searchParamsCrossMajor
                    )

                    5 -> SearchableCourses(
                        courseSystem = courseSystem,
                        sort = Sort.CrossYear,
                        searchParams = searchParamsCrossYear
                    )

                    6 -> SearchableCourses(
                        courseSystem = courseSystem,
                        sort = Sort.Major,
                        searchParams = searchParamsMajor
                    )
                }
            }
        }
    }

    courseSystem?.let {
        TimetableBottomSheet(
            visible = timetableBottomSheetVisible,
            onDismiss = { timetableBottomSheetVisible = false },
            courseSystem = it
        )

        LogsBottomSheet(
            visible = logsBottomSheetVisible,
            onDismiss = { logsBottomSheetVisible = false },
            courseSystem = it
        )

        InfoDialog(
            visible = infoDialogVisible,
            onDismiss = { infoDialogVisible = false },
            text = "${it.name}\n${it.start} - ${it.end}"
        )
    }

    TokenDialog(
        visible = tokenDialogVisible,
        onDismiss = { tokenDialogVisible = false },
        onConfirm = { eduSystem?.run { token = token(it); tokenDialogVisible = false } },
        token = token
    )

    SearchParamsBottomSheet(
        visible = searchParamsBottomSheetVisible,
        onDismiss = { searchParamsBottomSheetVisible = false },
        onConfirm = {
            when (tabPagerState.currentPage) {
                2 -> searchParamsPE = it
                3 -> searchParamsGeneral = it
                4 -> searchParamsCrossMajor = it
                5 -> searchParamsCrossYear = it
                6 -> searchParamsMajor = it
            }
            searchParamsBottomSheetVisible = false
        },
        searchParams = visibleSearchParams
    )
}


/**
 * 选课课表
 * @param visible
 * @param onDismiss
 * @param courseSystem
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TimetableBottomSheet(
    visible: Boolean,
    onDismiss: () -> Unit,
    courseSystem: CourseSystem
) {
    val tabPagerState = rememberTabPagerState(R.string.list, R.string.table)
    val overview = rememberRemote(key = courseSystem) { courseSystem.overview() }
    val myCourses = rememberRemoteList(key = courseSystem) { courseSystem.myCourses() }

    BottomSheet(visible = visible, onDismiss = onDismiss, skipPartiallyExpanded = true) {
        Column(modifier = Modifier.padding(dp4(4))) {
            overview?.run {
                pointInfos.forEach {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = it.name)
                        Text(text = "${it.have} / ${it.limit}")
                    }
                }
                Spacer(modifier = Modifier.height(dp4()))
                SubduedText(text = info)
                Spacer(modifier = Modifier.height(dp4(4)))
            }

            TabPager(state = tabPagerState, tabContent = { Text(text = getString(it)) }) { page ->
                when (page) {
                    0 -> {
                        val coroutineScope = rememberCoroutineScope()
                        var courseDeleteDialogVisible by remember { mutableStateOf(false) }
                        var visibleMyCourse by remember { mutableStateOf<MyCourse?>(null) }

                        LazyColumn(contentPadding = PaddingValues(dp4(2))) {
                            items(myCourses) {
                                ElevatedCard(onClick = {}, modifier = Modifier.padding(dp4(2))) {
                                    Row(
                                        modifier = Modifier.padding(dp4(4)),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Title(text = it.name)
                                            SubduedText(text = it.teacher)
                                            SubduedText(text = "${getString(R.string.id)} ${it.id}")
                                            SubduedText(text = "${getString(R.string.point)} ${it.point}")
                                            SubduedText(text = "${getString(R.string.type)} ${it.type}")
                                            if (it.classInfos.isNotEmpty()) {
                                                Spacer(modifier = Modifier.height(dp4()))
                                                SubduedText(
                                                    text = it.classInfos.joinToString("\n") {
                                                        "${it.weeksStr} ${it.dayOfWeek} ${it.section} ${it.area}"
                                                    }
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(dp4()))
                                        TextButton(
                                            onClick = {
                                                visibleMyCourse = it
                                                courseDeleteDialogVisible = true
                                            }
                                        ) {
                                            Text(
                                                text = getString(R.string.course_delete),
                                                color = MaterialTheme.colorScheme.error
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        CourseDeleteDialog(
                            visible = courseDeleteDialogVisible,
                            onDismiss = { courseDeleteDialogVisible = false },
                            onConfirm = {
                                visibleMyCourse?.run {
                                    coroutineScope.launchCatching {
                                        courseSystem.delete(id)
                                        myCourses.remove(this@run)
                                        Toast(R.string.deleted).show()
                                    }
                                }
                            },
                            courseName = visibleMyCourse?.name
                        )
                    }

                    1 -> {
                        val headerHeight = dp4(6)
                        val height = dp4(16)
                        val style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp)
                        val numRegex = Regex("\\d+")
                        val dayOfWeek = getString(R.string.day_of_week, "")
                        val daysOfWeek = getStringArray(R.array.days_of_week)
                        val daysOfWeekMapped = daysOfWeek.map { dayOfWeek + it }

                        Row(modifier = Modifier.padding(dp4(4))) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Section.entries.forEachIndexed { index, section ->
                                    Box(
                                        modifier = Modifier.height(
                                            if (index == 0) headerHeight else height
                                        ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = section.value, style = style)
                                    }
                                }
                            }
                            Column {
                                Row {
                                    daysOfWeek.forEach {
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(headerHeight),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = it,
                                                modifier = Modifier
                                            )
                                        }
                                    }
                                }
                                FlowColumn(maxItemsInEachColumn = 6) {
                                    (0..41).forEach { index ->
                                        val section = (index % 6 + 1) * 2 - 1
                                        val dayOfWeekIndex = index / 6

                                        Surface(
                                            onClick = {},
                                            modifier = Modifier
                                                .fillMaxWidth(1 / 7f - 0.001f)
                                                .height(height),
                                            shadowElevation = 0.1.dp
                                        ) {
                                            Box(
                                                modifier = Modifier.fillMaxSize(),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                myCourses.forEach { myCourse ->
                                                    if (
                                                        myCourse.classInfos.any { classInfo ->
                                                            daysOfWeekMapped[dayOfWeekIndex] == classInfo.dayOfWeek
                                                                    && numRegex
                                                                .findAll(classInfo.section)
                                                                .any { it.value.toInt() == section }
                                                        }
                                                    ) Text(
                                                        text = myCourse.name,
                                                        style = style,
                                                        maxLines = 3,
                                                        textAlign = TextAlign.Center,
                                                        overflow = TextOverflow.Ellipsis
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


/**
 * 课程删除确认
 * @param visible
 * @param onDismiss
 * @param onConfirm
 * @param courseName
 */
@Composable
private fun CourseDeleteDialog(
    visible: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    courseName: String?
) {
    DialogVisibility(visible = visible) {
        courseName?.let { name ->
            var value by remember { mutableStateOf("") }

            Dialog(
                text = {
                    SelectionContainer {
                        Text(text = getString(R.string.input_to_delete, name))
                    }
                    TextField(value = value, onValueChange = { value = it })
                },
                onDismiss = onDismiss,
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (value != name) Toast(R.string.input_does_not_match).show()
                            else onConfirm()
                        }
                    ) {
                        Text(text = getString(R.string.confirm))
                    }
                },
                dismissButton = {
                    TextButton(onClick = onDismiss) {
                        Text(text = getString(R.string.cancel))
                    }
                }
            )
        }
    }
}


/**
 * 退课日志
 * @param visible
 * @param onDismiss
 * @param courseSystem
 */
@Composable
private fun LogsBottomSheet(visible: Boolean, onDismiss: () -> Unit, courseSystem: CourseSystem) {
    BottomSheet(visible = visible, onDismiss = onDismiss, skipPartiallyExpanded = true) {
        val logs = rememberRemoteList(key = courseSystem) { courseSystem.log() }

        LazyColumn(contentPadding = PaddingValues(dp4(2))) {
            item {
                Title(text = getString(R.string.log), modifier = Modifier.padding(dp4(2)))
            }
            items(logs) {
                var expanded by remember { mutableStateOf(false) }

                ElevatedCard(
                    onClick = { expanded = !expanded },
                    modifier = Modifier.padding(dp4(2))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(dp4(4))
                    ) {
                        SubduedText(text = it.time)
                        SubduedText(text = "${it.operation} ${it.operator}")
                        SubduedText(text = it.courseName)
                        SubduedText(text = "${getString(R.string.id)} ${it.courseId}")
                        SubduedText(text = "${getString(R.string.teacher)} ${it.teacher}")
                        AnimatedVisibility(visible = expanded) {
                            Column {
                                SubduedText(text = "${getString(R.string.type)} ${it.courseType}")
                                SubduedText(text = "${getString(R.string.point)} ${it.point}")
                                SubduedText(
                                    text = it.classTime.joinToString("\n"),
                                    modifier = Modifier.padding(vertical = dp4())
                                )
                                SubduedText(text = it.courseSort)
                                if (it.desc.isNotBlank()) {
                                    SubduedText(text = it.desc)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


/**
 * 选课信息
 * @param visible
 * @param onDismiss
 * @param text
 */
@Composable
private fun InfoDialog(visible: Boolean, onDismiss: () -> Unit, text: String) {
    DialogVisibility(visible = visible) {
        Dialog(
            text = { Text(text = text) },
            onDismiss = onDismiss,
            contentHorizontalAlignment = Alignment.Start
        )
    }
}


/**
 * token 输入
 * @param visible
 * @param onDismiss
 * @param onConfirm
 * @param token
 */
@Composable
private fun TokenDialog(
    visible: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
    token: Token?
) {
    DialogVisibility(visible = visible) {
        var tokenValue by remember { mutableStateOf(token?.value ?: "") }

        Dialog(
            text = {
                TextField(value = tokenValue, onValueChange = { tokenValue = it })
                SubduedText(text = getString(R.string.token_input_tip))
            },
            onDismiss = onDismiss,
            confirmButton = {
                TextButton(onClick = { onConfirm(tokenValue) }) {
                    Text(text = getString(R.string.confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) { Text(text = getString(R.string.cancel)) }
            },
            contentHorizontalAlignment = Alignment.Start
        )
    }
}


/**
 * 不可搜索课程列表
 * @param courseSystem
 * @param sort
 */
@Composable
private fun UnsearchableCourses(courseSystem: CourseSystem, sort: Sort.Unsearchable) {
    val pager = remember(key1 = courseSystem) {
        Pager(15) { UnsearchableCoursesPagingSource(courseSystem, sort) }
    }
    Courses(pager = pager, courseSystem = courseSystem)
}


/**
 * 可搜索课程列表
 * @param courseSystem
 * @param sort
 * @param searchParams
 * @param predicate
 */
@Composable
private fun SearchableCourses(
    courseSystem: CourseSystem,
    sort: Sort.Searchable,
    searchParams: SearchParams,
    predicate: ((Course) -> Boolean)? = null
) {
    val pager = remember(key1 = courseSystem, key2 = searchParams) {
        Pager(15) { SearchableCoursesPagingSource(courseSystem, sort, searchParams, predicate) }
    }
    Courses(pager = pager, courseSystem = courseSystem)
}


/**
 * 课程列表
 * @param pager
 * @param courseSystem
 */
@Composable
private fun Courses(pager: Pager<Int, Course>, courseSystem: CourseSystem) {
    val coroutineScope = rememberCoroutineScope()
    val items = pager.flow.collectAsLazyPagingItems()

    var courseBottomSheetVisible by remember { mutableStateOf(false) }
    var priorityDialogVisible by remember { mutableStateOf(false) }
    var courseDeleteDialog by remember { mutableStateOf(false) }
    var visibleCourse by remember { mutableStateOf<Course?>(null) }

    LazyColumn(contentPadding = PaddingValues(dp4(2))) {
        items(items) {
            ElevatedCard(
                onClick = {
                    visibleCourse = it
                    courseBottomSheetVisible = true
                },
                modifier = Modifier.padding(dp4(2))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(dp4(4)),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Title(text = it.name, maxLines = 1)
                        Spacer(modifier = Modifier.height(dp4()))

                        CompositionLocalProvider(
                            value = LocalTextStyle provides MaterialTheme.typography.bodySmall
                        ) {
                            Row {
                                IconText(
                                    text = it.id,
                                    leadingIcon = Icons.Rounded.Numbers,
                                    modifier = Modifier.weight(1f)
                                )
                                Spacer(modifier = Modifier.width(dp4(2)))

                                IconText(
                                    text = it.point,
                                    leadingIcon = Icons.Rounded.Star,
                                    modifier = Modifier.weight(1f),
                                    leadingText = getString(R.string.point)
                                )
                            }

                            Row(horizontalArrangement = Arrangement.SpaceBetween) {
                                IconText(
                                    text = it.teacher,
                                    leadingIcon = Icons.Rounded.AccountBox,
                                    modifier = Modifier.weight(1f),
                                    leadingText = getString(R.string.teacher),
                                    maxLines = 1
                                )
                                Spacer(modifier = Modifier.width(dp4(2)))

                                IconText(
                                    text = "${it.remaining} / ${it.total}",
                                    leadingIcon = Icons.Rounded.PersonOutline,
                                    modifier = Modifier.weight(1f),
                                    leadingText = getString(R.string.size)
                                )
                            }
                            Spacer(modifier = Modifier.height(dp4()))

                            IconText(
                                text = it.area,
                                leadingIcon = Icons.Rounded.LocationOn,
                                leadingText = getString(R.string.area),
                                maxLines = 1
                            )
                            IconText(
                                text = it.time,
                                leadingIcon = Icons.Rounded.Timelapse,
                                leadingText = getString(R.string.time),
                                maxLines = 1
                            )

                            if (it.status.isNotBlank()) {
                                Spacer(modifier = Modifier.height(dp4()))
                                SubduedText(text = it.status)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.width(dp4()))

                    TextButton(
                        onClick = {
                            if (it.selectable) {
                                coroutineScope.launchCatching {
                                    courseSystem.select(it.operateId, it.sort, null)
                                }
                            } else {
                                visibleCourse = it
                                priorityDialogVisible = true
                            }
                        },
                        enabled = !it.selected
                    ) {
                        Text(
                            text = getString(
                                if (it.selected) R.string.selected else R.string.course_select
                            )
                        )
                    }
                }
            }
        }
    }

    CourseBottomSheet(
        visible = courseBottomSheetVisible,
        onDismiss = { courseBottomSheetVisible = false },
        course = visibleCourse,
        onSelect = {
            visibleCourse?.run {
                if (selectable) {
                    coroutineScope.launchCatching {
                        courseSystem.select(operateId, sort, null)
                        items.refresh()
                    }
                } else {
                    priorityDialogVisible = true
                }
            }
        },
        onDelete = { courseDeleteDialog = true }
    )

    CourseDeleteDialog(
        visible = courseDeleteDialog,
        onDismiss = { courseDeleteDialog = false },
        onConfirm = {
            visibleCourse?.run {
                coroutineScope.launchCatching {
                    courseSystem.delete(operateId)
                    items.refresh()
                    Toast(R.string.deleted).show()
                    courseDeleteDialog = false
                }
            }
        },
        courseName = visibleCourse?.name
    )

    PriorityDialog(
        visible = priorityDialogVisible,
        onDismiss = { priorityDialogVisible = false },
        onConfirm = { priority ->
            visibleCourse?.let {
                coroutineScope.launchCatching {
                    courseSystem.select(it.operateId, it.sort, priority)
                    items.refresh()
                    Toast(R.string.selected).show()
                    priorityDialogVisible = false
                }
            }
        }
    )
}


/**
 * 志愿选择
 * @param visible
 * @param onDismiss
 * @param onConfirm
 */
@Composable
private fun PriorityDialog(visible: Boolean, onDismiss: () -> Unit, onConfirm: (Priority) -> Unit) {
    DialogVisibility(visible = visible) {
        val priorities = getStringArray(R.array.priorities)
        var priority by remember { mutableIntStateOf(0) }

        Dialog(
            title = { Text(text = getString(R.string.priority)) },
            text = {
                priorities.forEachIndexed { index, item ->
                    RadioButton(
                        selected = priority == index,
                        onClick = { priority = index },
                        label = item
                    )
                }
            },
            onDismiss = onDismiss,
            confirmButton = {
                TextButton(
                    onClick = {
                        onConfirm(
                            when (priority) {
                                0 -> Priority.First
                                1 -> Priority.First
                                else -> Priority.Third
                            }
                        )
                    }
                ) {
                    Text(text = getString(R.string.confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) { Text(text = getString(R.string.cancel)) }
            },
            contentHorizontalAlignment = Alignment.Start
        )
    }
}


/**
 * 课程详情
 * @param visible
 * @param onDismiss
 * @param course
 * @param onSelect
 * @param onDelete
 */
@Composable
private fun CourseBottomSheet(
    visible: Boolean,
    onDismiss: () -> Unit,
    course: Course?,
    onSelect: () -> Unit,
    onDelete: () -> Unit
) {
    BottomSheet(visible = visible, onDismiss = onDismiss, skipPartiallyExpanded = true) {
        course?.run {
            var courseDeleteDialog by remember { mutableStateOf(false) }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.5f)
                    .padding(dp4(4)),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    item {
                        Title(text = name)
                        Spacer(modifier = Modifier.padding(dp4()))
                    }
                    item {
                        SubduedText(text = "${getString(R.string.id)} $id")
                        SubduedText(text = "${getString(R.string.size)} $remaining / $total")
                        SubduedText(text = "${getString(R.string.department)} $department")
                        SubduedText(text = "${getString(R.string.class_hours)} $classHours")
                        SubduedText(text = "${getString(R.string.exam_mode)} $examMode")
                        if (status.isNotBlank()) {
                            SubduedText(text = status)
                        }
                        Spacer(modifier = Modifier.padding(dp4()))
                    }
                    item {
                        CompositionLocalProvider(
                            value = LocalTextStyle provides MaterialTheme.typography.bodyMedium
                        ) {
                            IconText(
                                text = teacher,
                                leadingIcon = Icons.Rounded.AccountBox,
                                leadingText = getString(R.string.teacher)
                            )
                            IconText(
                                text = time,
                                leadingIcon = Icons.Rounded.Timelapse,
                                leadingText = getString(R.string.time)
                            )
                            IconText(
                                text = area,
                                leadingIcon = Icons.Rounded.LocationOn,
                                leadingText = getString(R.string.area)
                            )
                            IconText(
                                text = getString(campus.nameResId),
                                leadingIcon = Icons.Rounded.Star,
                                leadingText = getString(R.string.campus)
                            )
                            IconText(
                                text = point,
                                leadingIcon = Icons.Rounded.Star,
                                modifier = Modifier.weight(1f),
                                leadingText = getString(R.string.point)
                            )
                        }
                    }
                }

                ElevatedButton(
                    onClick = {
                        if (course.selected) {
                            courseDeleteDialog = true
                        } else onSelect()
                    },
                    colors = ButtonDefaults.elevatedButtonColors(
                        contentColor = if (course.selected) MaterialTheme.colorScheme.error
                        else Color.Unspecified
                    ),
                    modifier = Modifier.padding(dp4(4))
                ) {
                    Text(
                        text = getString(
                            if (course.selected) R.string.course_delete
                            else R.string.course_select
                        )
                    )
                }
            }

            CourseDeleteDialog(
                visible = courseDeleteDialog,
                onDismiss = { courseDeleteDialog = false },
                onConfirm = onDelete,
                courseName = course.name
            )
        }
    }
}


/**
 * 搜索参数设定
 * @param visible
 * @param onDismiss
 * @param onConfirm
 * @param searchParams
 */
@Composable
private fun SearchParamsBottomSheet(
    visible: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (SearchParams) -> Unit,
    searchParams: SearchParams?
) {
    BottomSheet(visible = visible, onDismiss = onDismiss, skipPartiallyExpanded = true) {
        searchParams?.let { searchParams ->
            val daysOfWeek = getStringArray(R.array.days_of_week)

            var name by remember { mutableStateOf(searchParams.name) }
            var teacher by remember { mutableStateOf(searchParams.teacher) }
            var dayOfWeek by remember { mutableStateOf(searchParams.dayOfWeek) }
            var section by remember { mutableStateOf(searchParams.section) }
            var emptyOnly by remember { mutableStateOf(searchParams.emptyOnly) }
            var filterConflicting by remember { mutableStateOf(searchParams.filterConflicting) }
            var campus by remember { mutableStateOf(searchParams.campus) }

            Column(modifier = Modifier.padding(dp4(4))) {
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(text = getString(R.string.course)) }
                )
                TextField(
                    value = teacher,
                    onValueChange = { teacher = it },
                    label = { Text(text = getString(R.string.teacher)) }
                )

                Text(text = searchParams.dayOfWeek?.name ?: "")
                Options(
                    name = getString(R.string.day_of_week, ""),
                    onSelect = { dayOfWeek = if (it == 0) null else DayOfWeek.of(it) },
                    options = listOf(
                        getString(R.string.all),
                        *DayOfWeek.entries.map { daysOfWeek[it.ordinal] }.toTypedArray()
                    ),
                    toString = { it },
                    selectedIndex = dayOfWeek?.ordinal?.plus(1) ?: 0
                )
                Options(
                    name = getString(R.string.section),
                    onSelect = { section = Section.entries[it] },
                    options = Section.entries,
                    toString = { if (it == Section.Unspecified) getString(R.string.all) else it.value },
                    selectedIndex = section.ordinal
                )
                Options(
                    name = getString(R.string.campus),
                    onSelect = { campus = if (it == 0) null else Campus.getById(it) },
                    options = listOf(
                        R.string.all,
                        *Campus.values.map { it.nameResId }.toTypedArray()
                    ),
                    toString = { getString(it) },
                    selectedIndex = campus?.id ?: 0
                )
                CheckBox(
                    checked = emptyOnly,
                    onCheckedChange = { emptyOnly = it },
                    label = getString(R.string.filtering_no_seats)
                )
                CheckBox(
                    checked = filterConflicting,
                    onCheckedChange = { filterConflicting = it },
                    label = getString(R.string.filter_conflicting)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(dp4(4)),
                    contentAlignment = Alignment.Center
                ) {
                    ElevatedButton(
                        onClick = {
                            onConfirm(
                                SearchParams(
                                    name, teacher, dayOfWeek, section, emptyOnly,
                                    filterConflicting, campus
                                )
                            )
                        }
                    ) {
                        Text(text = getString(R.string.confirm))
                    }
                }
            }
        }
    }
}


/**
 * 不可搜索课程数据源
 * @property courseSystem
 * @property sort
 */
private class UnsearchableCoursesPagingSource(
    private val courseSystem: CourseSystem,
    private val sort: Sort.Unsearchable
) : PagingSource<Course>() {
    override suspend fun loadCatching(params: LoadParams<Int>) =
        Page(courseSystem.list(sort, params.key!!), params)
}


/**
 * 可搜索课程数据源
 * @property courseSystem
 * @property sort
 * @property searchParams
 * @property predicate 课程项过滤
 */
private class SearchableCoursesPagingSource(
    private val courseSystem: CourseSystem,
    private val sort: Sort.Searchable,
    private val searchParams: SearchParams,
    private val predicate: ((Course) -> Boolean)?
) : PagingSource<Course>() {
    override suspend fun loadCatching(params: LoadParams<Int>) =
        Page(courseSystem.search(sort, searchParams, params.key!!), params)
}


@Composable
private fun <T> Options(
    name: String,
    onSelect: (Int) -> Unit,
    options: List<T>,
    toString: (T) -> String,
    selectedIndex: Int
) {
    var expanded by remember { mutableStateOf(false) }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = name)
        Spacer(modifier = Modifier.width(dp4(2)))
        TextButton(onClick = { expanded = true }) {
            Text(text = toString(options[selectedIndex]))
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEachIndexed { index, item ->
                DropdownMenuItem(
                    text = { Text(text = toString(item)) },
                    onClick = { onSelect(index); expanded = false },
                    selected = selectedIndex == index
                )
            }
        }
    }
}