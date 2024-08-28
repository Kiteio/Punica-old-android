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
import androidx.compose.material.icons.rounded.AccountBalance
import androidx.compose.material.icons.rounded.AreaChart
import androidx.compose.material.icons.rounded.AssignmentInd
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.DoorFront
import androidx.compose.material.icons.rounded.Filter
import androidx.compose.material.icons.rounded.Mail
import androidx.compose.material.icons.rounded.Numbers
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PhoneAndroid
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.Stars
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material.icons.rounded.Wc
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.compose.collectAsLazyPagingItems
import compose.icons.SimpleIcons
import compose.icons.simpleicons.Tencentqq
import compose.icons.simpleicons.Wechat
import org.kiteio.punica.R
import org.kiteio.punica.candy.catching
import org.kiteio.punica.edu.system.EduSystem
import org.kiteio.punica.edu.system.api.Teacher
import org.kiteio.punica.edu.system.api.TeacherItem
import org.kiteio.punica.edu.system.api.teacher
import org.kiteio.punica.edu.system.api.teacherList
import org.kiteio.punica.getString
import org.kiteio.punica.ui.LocalViewModel
import org.kiteio.punica.ui.component.BottomSheet
import org.kiteio.punica.ui.component.IconText
import org.kiteio.punica.ui.component.LazyPagingColumn
import org.kiteio.punica.ui.component.NavBackTopAppBar
import org.kiteio.punica.ui.component.Pager
import org.kiteio.punica.ui.component.PagingSource
import org.kiteio.punica.ui.component.ScaffoldColumn
import org.kiteio.punica.ui.component.SearchBar
import org.kiteio.punica.ui.component.SubduedText
import org.kiteio.punica.ui.component.Title
import org.kiteio.punica.ui.component.items
import org.kiteio.punica.ui.dp4
import org.kiteio.punica.ui.navigation.Route
import org.kiteio.punica.ui.runWithReLogin

/**
 * 教师信息
 */
@Composable
fun TeacherInfoScreen() {
    val eduSystem = LocalViewModel.current.eduSystem
    var teacherName by remember { mutableStateOf("") }
    val pager = remember(key1 = eduSystem, key2 = teacherName) {
        Pager(20) { TeacherInfoPagingSource(teacherName, eduSystem) }
    }
    val teacherItems = pager.flow.collectAsLazyPagingItems()

    var teacherBottomSheetVisible by remember { mutableStateOf(false) }
    var visibleTeacherId by remember { mutableStateOf<String?>(null) }

    ScaffoldColumn(
        topBar = {
            NavBackTopAppBar(route = Route.Module.TeacherInfo, shadowElevation = 0.dp)
        }
    ) {
        Surface(shadowElevation = 1.dp) {
            SearchBar(
                value = teacherName,
                onValueChange = { teacherName = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dp4(4)),
                placeholder = {
                    Text(text = getString(R.string.input, getString(R.string.teacher_name)))
                }
            )
        }

        LazyPagingColumn(
            loadState = teacherItems.loadState,
            contentPadding = PaddingValues(dp4(2))
        ) {
            items(teacherItems) {
                ElevatedCard(
                    onClick = {
                        visibleTeacherId = it.id
                        teacherBottomSheetVisible = true
                    },
                    modifier = Modifier.padding(dp4(2))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(dp4(4))
                    ) {
                        Title(text = it.name)
                        Spacer(modifier = Modifier.height(dp4()))
                        CompositionLocalProvider(
                            value = LocalTextStyle provides MaterialTheme.typography.bodyMedium
                        ) {
                            IconText(
                                text = it.id,
                                leadingIcon = Icons.Rounded.Numbers,
                                leadingText = getString(R.string.job_number)
                            )
                            IconText(
                                text = it.department,
                                leadingIcon = Icons.Rounded.AccountBalance,
                                leadingText = getString(R.string.department)
                            )
                        }
                    }
                }
            }
        }
    }

    TeacherBottomSheet(
        visible = teacherBottomSheetVisible,
        onDismiss = { teacherBottomSheetVisible = false },
        eduSystem = eduSystem,
        id = visibleTeacherId
    )
}


/**
 * 教师基础信息 [PagingSource]
 * @property teacherName 教师名
 * @property eduSystem
 */
private class TeacherInfoPagingSource(
    private val teacherName: String,
    private val eduSystem: EduSystem?
) : PagingSource<TeacherItem>() {
    override suspend fun loadCatching(params: LoadParams<Int>) = eduSystem?.run {
        try {
            teacherList(teacherName, params.key!!)
        } catch (e: Throwable) {
            reLogin()
            teacherList(name, params.key!!)
        }.let { Page(it, params) }
    } ?: error(getString(R.string.not_logged_in))
}


/**
 * 教师信息
 * @param visible
 * @param onDismiss
 * @param eduSystem
 * @param id 工号
 */
@Composable
fun TeacherBottomSheet(
    visible: Boolean,
    onDismiss: () -> Unit,
    eduSystem: EduSystem?,
    id: String?
) {
    BottomSheet(visible = visible, onDismiss = onDismiss, skipPartiallyExpanded = true) {
        var teacher by remember { mutableStateOf<Teacher?>(null) }

        LaunchedEffect(key1 = eduSystem, key2 = id) {
            id?.let {
                teacher = catching<Teacher?> { eduSystem?.runWithReLogin { teacher(it) } }
            }
        }

        LazyColumn(contentPadding = PaddingValues(dp4(4))) {
            teacher?.run {
                item {
                    Title(text = name)
                    Spacer(modifier = Modifier.height(dp4(2)))
                }

                // 最想对学生说的话
                item {
                    SubduedText(text = slogan)
                    Spacer(modifier = Modifier.height(dp4(2)))
                }

                // 教学理念
                item {
                    SubduedText(text = philosophy)
                    Spacer(modifier = Modifier.height(dp4(2)))
                }

                item {
                    IconText(
                        text = gender,
                        leadingIcon = Icons.Rounded.Wc,
                        leadingText = getString(R.string.gender)
                    )
                    IconText(
                        text = nation,
                        leadingIcon = Icons.Rounded.AreaChart,
                        leadingText = getString(R.string.nation)
                    )
                    IconText(
                        text = politics,
                        leadingIcon = Icons.Rounded.Person,
                        leadingText = getString(R.string.politics)
                    )
                    Spacer(modifier = Modifier.height(dp4(4)))
                }



                item {
                    IconText(
                        text = degree,
                        leadingIcon = Icons.Rounded.School,
                        leadingText = getString(R.string.degree)
                    )
                    IconText(
                        text = qualifications,
                        leadingIcon = Icons.Rounded.Verified,
                        leadingText = getString(R.string.qualification)
                    )
                    IconText(
                        text = field,
                        leadingIcon = Icons.Rounded.Filter,
                        leadingText = getString(R.string.field)
                    )
                    Spacer(modifier = Modifier.height(dp4(4)))
                }

                item {
                    IconText(
                        text = phoneNumber,
                        leadingIcon = Icons.Rounded.PhoneAndroid,
                        leadingText = getString(R.string.phone_number)
                    )
                    IconText(
                        text = qQ,
                        leadingIcon = SimpleIcons.Tencentqq,
                        leadingText = getString(R.string.qq)
                    )
                    IconText(
                        text = weChat,
                        leadingIcon = SimpleIcons.Wechat,
                        leadingText = getString(R.string.wechat)
                    )
                    IconText(
                        text = email,
                        leadingIcon = Icons.Rounded.Mail,
                        leadingText = getString(R.string.email)
                    )
                    Spacer(modifier = Modifier.height(dp4(4)))
                }

                item {
                    IconText(
                        text = duty,
                        leadingIcon = Icons.Rounded.AssignmentInd,
                        leadingText = getString(R.string.duty)
                    )
                    IconText(
                        text = title,
                        leadingIcon = Icons.Rounded.Stars,
                        leadingText = getString(R.string.title)
                    )
                    IconText(
                        text = category,
                        leadingIcon = Icons.Rounded.Category,
                        leadingText = getString(R.string.faculty_category)
                    )
                    IconText(
                        text = department,
                        leadingIcon = Icons.Rounded.AccountBalance,
                        leadingText = getString(R.string.department_faculty_large)
                    )
                    IconText(
                        text = office,
                        leadingIcon = Icons.Rounded.DoorFront,
                        leadingText = getString(R.string.office_faculty)
                    )
                    Spacer(modifier = Modifier.height(dp4(4)))
                }

                item {
                    IconText(
                        text = "",
                        leadingIcon = Icons.Rounded.Description,
                        leadingText = getString(R.string.biography)
                    )
                    Text(
                        text = introduction,
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp)
                    )
                    Spacer(modifier = Modifier.height(dp4(4)))
                }

                listOf(taught, teaching).forEachIndexed { index, item ->
                    item {
                        Text(
                            text = getString(
                                if (index == 0) R.string.teaching_history
                                else R.string.teaching_future
                            ),
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }

                    items(item) {
                        ElevatedCard(modifier = Modifier.padding(dp4(2))) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(dp4(2)),
                            ) {
                                Title(text = it.name, style = MaterialTheme.typography.bodyMedium)
                                Spacer(modifier = Modifier.height(dp4()))
                                SubduedText(text = it.semester.toString())
                                SubduedText(text = it.sort)
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(dp4(4)))
                    }
                }
            }
        }
    }
}