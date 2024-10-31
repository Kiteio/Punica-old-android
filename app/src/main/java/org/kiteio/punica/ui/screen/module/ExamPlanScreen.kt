package org.kiteio.punica.ui.screen.module

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
import androidx.compose.material.icons.rounded.AccessTime
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.MoreTime
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.kiteio.punica.R
import org.kiteio.punica.candy.LocalDateTimeNow
import org.kiteio.punica.candy.minutesUntil
import org.kiteio.punica.datastore.ExamPlans
import org.kiteio.punica.edu.system.EduSystem
import org.kiteio.punica.edu.system.api.ExamPlanItem
import org.kiteio.punica.edu.system.api.examPlan
import org.kiteio.punica.getString
import org.kiteio.punica.ui.collectAsEduSystemIdentified
import org.kiteio.punica.ui.component.IconText
import org.kiteio.punica.ui.component.NavBackTopAppBar
import org.kiteio.punica.ui.component.ScaffoldBox
import org.kiteio.punica.ui.component.SubduedText
import org.kiteio.punica.ui.component.Title
import org.kiteio.punica.ui.dp4
import org.kiteio.punica.ui.navigation.Route
import org.kiteio.punica.ui.rememberLastUsername
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * 考试安排
 */
@Composable
fun ExamPlanScreen() {
    val examPlan = ExamPlans.collectAsEduSystemIdentified(
        id = rememberLastUsername(EduSystem.semester)
    ) { examPlan() }

    ScaffoldBox(
        topBar = {
            NavBackTopAppBar(
                route = Route.Module.ExamPlan,
                actions = {
                    examPlan?.semester?.let {
                        TextButton(onClick = {}) {
                            Text(text = "${examPlan.semester}")
                        }
                    }
                }
            )
        }
    ) {
        LazyColumn(contentPadding = PaddingValues(dp4(2))) {
            examPlan?.run {
                items(
                    items.sortedBy {
                        LocalDateTimeNow.minutesUntil(LocalDateTime(it.time.first)) < 0
                    }
                ) {
                    ExamPlanItem(
                        examPlanItem = it,
                        enabled = LocalDateTimeNow.minutesUntil(LocalDateTime(it.time.first)) > 0
                    )
                }
            }
        }
    }
}


/**
 * 考试安排项
 * @param examPlanItem
 * @param enabled
 */
@Composable
private fun ExamPlanItem(examPlanItem: ExamPlanItem, enabled: Boolean) = with(examPlanItem) {
    ElevatedCard(onClick = {}, enabled = enabled, modifier = Modifier.padding(dp4(2))) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dp4(4))
        ) {
            Row {
                Column(modifier = Modifier.weight(1f)) {
                    Title(text = name)
                    SubduedText(text = id)
                }
                Spacer(modifier = Modifier.width(dp4(4)))
                Column {
                    SubduedText(text = campus)
                    SubduedText(
                        text = getString(
                            R.string.minutes,
                            LocalDateTime(time.first)
                                .minutesUntil(LocalDateTime(time.second))
                        )
                    )
                }
            }
            Spacer(modifier = Modifier.height(dp4()))
            IconText(
                text = time.first,
                leadingIcon = Icons.Rounded.AccessTime,
                leadingText = getString(R.string.start)
            )
            IconText(
                text = time.second,
                leadingIcon = Icons.Rounded.MoreTime,
                leadingText = getString(R.string.end)
            )
            IconText(
                text = area,
                leadingIcon = Icons.Rounded.LocationOn,
                leadingText = getString(R.string.area)
            )
        }
    }
}


/**
 * 将时间字符串格式化为 [LocalDateTime]
 * @param text
 * @return [LocalDateTime]
 */
private fun LocalDateTime(text: String) =
    LocalDateTime.parse(text, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))