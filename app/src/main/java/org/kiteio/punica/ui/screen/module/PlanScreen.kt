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
import androidx.compose.material.icons.rounded.Numbers
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.kiteio.punica.R
import org.kiteio.punica.datastore.Plans
import org.kiteio.punica.edu.foundation.Semester
import org.kiteio.punica.edu.system.EduSystem
import org.kiteio.punica.edu.system.api.plan
import org.kiteio.punica.getString
import org.kiteio.punica.getStringArray
import org.kiteio.punica.ui.collectAsEduSystemIdentified
import org.kiteio.punica.ui.component.IconText
import org.kiteio.punica.ui.component.NavBackTopAppBar
import org.kiteio.punica.ui.component.ScaffoldBox
import org.kiteio.punica.ui.component.SubduedText
import org.kiteio.punica.ui.component.TabPager
import org.kiteio.punica.ui.component.Title
import org.kiteio.punica.ui.component.rememberTabPagerState
import org.kiteio.punica.ui.dp4
import org.kiteio.punica.ui.navigation.Route
import org.kiteio.punica.ui.rememberLastUsername

/**
 * 执行计划
 */
@Composable
fun PlanScreen() {
    val plan = Plans.collectAsEduSystemIdentified { plan() }

    val lastUsername = rememberLastUsername()
    val firstHalf = getString(R.string.first_half)
    val lastHalf = getString(R.string.last_half)
    val grades = getStringArray(R.array.grades).flatMap { listOf(it + firstHalf, it + lastHalf) }
        .toTypedArray()
    val tabPagerState = rememberTabPagerState(getString(R.string.all), *grades)
    val semesters = remember(lastUsername) { lastUsername?.let { Semester.listFor(it) } }

    LaunchedEffect(key1 = semesters) {
        semesters?.forEachIndexed { index, item ->
            if (item == EduSystem.semester) tabPagerState.scrollToPage(index + 1)
        }
    }

    ScaffoldBox(topBar = { NavBackTopAppBar(route = Route.Module.Plan, shadowElevation = 0.dp) }) {
        TabPager(
            state = tabPagerState,
            tabContent = { Text(text = it) }
        ) { page ->
            LazyColumn(contentPadding = PaddingValues(dp4(2))) {
                plan?.run {
                    items(if (page == 0) items else items.filter {
                        it.semester == semesters?.get(page - 1)
                    }) {
                        ElevatedCard(onClick = {}, modifier = Modifier.padding(dp4(2))) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(dp4(4))
                            ) {
                                Title(text = it.name)
                                SubduedText(text = "${it.semester}")
                                Spacer(modifier = Modifier.height(dp4()))

                                CompositionLocalProvider(
                                    value = LocalTextStyle provides MaterialTheme.typography.bodyMedium
                                ) {
                                    IconText(
                                        text = it.id,
                                        leadingIcon = Icons.Rounded.Numbers,
                                        leadingText = getString(R.string.id)
                                    )
                                    IconText(
                                        text = it.point,
                                        leadingIcon = Icons.Rounded.Star,
                                        leadingText = getString(R.string.point)
                                    )
                                }
                                Spacer(modifier = Modifier.height(dp4()))

                                SubduedText(text = "${it.type} | ${it.examMode}")
                                SubduedText(text = it.department)
                            }
                        }
                    }
                }
            }
        }
    }
}