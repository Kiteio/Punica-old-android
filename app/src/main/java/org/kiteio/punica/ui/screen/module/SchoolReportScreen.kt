package org.kiteio.punica.ui.screen.module

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MergeType
import androidx.compose.material.icons.automirrored.rounded.ReceiptLong
import androidx.compose.material.icons.automirrored.rounded.Sort
import androidx.compose.material.icons.rounded.AddChart
import androidx.compose.material.icons.rounded.AllOut
import androidx.compose.material.icons.rounded.Explicit
import androidx.compose.material.icons.rounded.HourglassBottom
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Numbers
import androidx.compose.material.icons.rounded.Score
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Timelapse
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.kiteio.punica.R
import org.kiteio.punica.datastore.SchoolReports
import org.kiteio.punica.edu.system.api.SchoolReportItem
import org.kiteio.punica.edu.system.api.schoolReport
import org.kiteio.punica.getString
import org.kiteio.punica.ui.LocalViewModel
import org.kiteio.punica.ui.collectAsIdentified
import org.kiteio.punica.ui.component.BottomSheet
import org.kiteio.punica.ui.component.Dialog
import org.kiteio.punica.ui.component.DialogVisibility
import org.kiteio.punica.ui.component.Icon
import org.kiteio.punica.ui.component.IconText
import org.kiteio.punica.ui.component.NavBackTopAppBar
import org.kiteio.punica.ui.component.ScaffoldColumn
import org.kiteio.punica.ui.component.SearchBar
import org.kiteio.punica.ui.component.SubduedText
import org.kiteio.punica.ui.component.TabPager
import org.kiteio.punica.ui.component.Title
import org.kiteio.punica.ui.component.rememberTabPagerState
import org.kiteio.punica.ui.dp4
import org.kiteio.punica.ui.navigation.Route

/**
 * 课程成绩
 */
@Composable
fun SchoolReportScreen() {
    val eduSystem = LocalViewModel.current.eduSystem
    val schoolReport = SchoolReports.collectAsIdentified { eduSystem?.schoolReport() }
    var evaluationDialogVisible by remember { mutableStateOf(false) }
    val tabPagerState = rememberTabPagerState(R.string.newest, R.string.all)
    var detailBottomSheetVisible by remember { mutableStateOf(false) }
    var visibleSchoolReportItem by remember { mutableStateOf<SchoolReportItem?>(null) }

    var query by remember { mutableStateOf("") }

    ScaffoldColumn(
        topBar = {
            NavBackTopAppBar(
                route = Route.Module.SchoolReport,
                shadowElevation = 0.dp,
                actions = {
                    IconButton(onClick = { evaluationDialogVisible = true }) {
                        Icon(imageVector = Icons.Rounded.Info)
                    }
                }
            )
        }
    ) {
        Surface(shadowElevation = 1.dp) {
            SearchBar(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dp4(4)),
                placeholder = {
                    Text(
                        text = getString(
                            R.string.input,
                            buildString {
                                append(getString(R.string.course))
                                append(getString(R.string.id), "、")
                                append(getString(R.string.name))
                            }
                        )
                    )
                }
            )
        }
        schoolReport?.run {
            TabPager(state = tabPagerState, tabContent = { Text(text = getString(it)) }) { page ->
                val schoolReportItems = when (page) {
                    0 -> mutableListOf<SchoolReportItem>().apply {
                        val semester = items.firstOrNull()?.semester

                        for (index in items.indices)
                            if (items[index].semester == semester) add(items[index])
                            else break
                    }

                    else -> items
                }

                LazyColumn(contentPadding = PaddingValues(dp4(2))) {
                    items(
                        if (query.isBlank()) schoolReportItems
                        else schoolReportItems.filter {
                            it.name.contains(query) || it.id.contains(query)
                        }
                    ) {
                        SchoolReportItem(
                            schoolReportItem = it,
                            onClick = {
                                detailBottomSheetVisible = true
                                visibleSchoolReportItem = it
                            },
                            modifier = Modifier.padding(dp4(2))
                        )
                    }
                }
            }
        }
    }

    EvaluationDialog(
        visible = evaluationDialogVisible,
        onDismiss = { evaluationDialogVisible = false },
        evaluation = schoolReport?.evaluation
    )

    DetailBottomSheet(
        visible = detailBottomSheetVisible,
        onDismiss = { detailBottomSheetVisible = false },
        schoolReportItem = visibleSchoolReportItem
    )
}


/**
 * 课程成绩项
 * @param schoolReportItem
 * @param onClick
 * @param modifier
 */
@Composable
private fun SchoolReportItem(
    schoolReportItem: SchoolReportItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) = with(schoolReportItem) {
    ElevatedCard(onClick = onClick, modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dp4(4)),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Title(text = name)
                Spacer(modifier = Modifier.height(dp4()))
                SubduedText(text = id)
                Spacer(modifier = Modifier.height(dp4()))
                SubduedText(text = "$semester")

                // 无需翻译
                if (examSort != "正常考试") SubduedText(text = examSort)
            }
            Spacer(modifier = Modifier.width(dp4(4)))
            Title(text = score)
        }
    }
}


/**
 * 系统评估信息
 * @param visible
 * @param onDismiss
 * @param evaluation
 */
@Composable
private fun EvaluationDialog(visible: Boolean, onDismiss: () -> Unit, evaluation: String?) {
    DialogVisibility(visible = visible) {
        Dialog(
            text = { evaluation?.let { Text(text = it) } },
            onDismiss = onDismiss
        )
    }
}


/**
 * 课程成绩详情
 * @param visible
 * @param onDismiss
 * @param schoolReportItem
 */
@Composable
private fun DetailBottomSheet(
    visible: Boolean,
    onDismiss: () -> Unit,
    schoolReportItem: SchoolReportItem?
) {
    BottomSheet(visible = visible, onDismiss = onDismiss, skipPartiallyExpanded = true) {
        schoolReportItem?.run {
            Column(modifier = Modifier.padding(dp4(4))) {
                Title(text = name)
                Spacer(modifier = Modifier.height(dp4()))
                SubduedText(text = "$semester")
                Spacer(modifier = Modifier.height(dp4(4)))

                IconText(
                    text = id,
                    leadingIcon = Icons.Rounded.Numbers,
                    leadingText = getString(R.string.id)
                )
                IconText(
                    text = sort,
                    leadingIcon = Icons.AutoMirrored.Rounded.Sort,
                    leadingText = getString(R.string.sort)
                )
                IconText(
                    text = type,
                    leadingIcon = Icons.AutoMirrored.Rounded.MergeType,
                    leadingText = getString(R.string.type)
                )
                IconText(
                    text = point,
                    leadingIcon = Icons.Rounded.Star,
                    leadingText = getString(R.string.point)
                )
                IconText(
                    text = score,
                    leadingIcon = Icons.Rounded.Score,
                    leadingText = getString(R.string.score)
                )
                Spacer(modifier = Modifier.height(dp4(4)))

                IconText(
                    text = usualScore,
                    leadingIcon = Icons.Rounded.Timelapse,
                    leadingText = getString(R.string.usual)
                )
                IconText(
                    text = experimentScore,
                    leadingIcon = Icons.Rounded.Explicit,
                    leadingText = getString(R.string.experiment)
                )
                IconText(
                    text = examScore,
                    leadingIcon = Icons.AutoMirrored.Rounded.ReceiptLong,
                    leadingText = getString(R.string.exam)
                )
                Spacer(modifier = Modifier.height(dp4(4)))

                IconText(
                    text = classHours,
                    leadingIcon = Icons.Rounded.HourglassBottom,
                    leadingText = getString(R.string.class_hours)
                )
                IconText(
                    text = examMode,
                    leadingIcon = Icons.Rounded.AddChart,
                    leadingText = getString(R.string.exam_mode)
                )
                IconText(
                    text = examSort,
                    leadingIcon = Icons.Rounded.AllOut,
                    leadingText = getString(R.string.exam_sort)
                )
            }
        }
    }
}