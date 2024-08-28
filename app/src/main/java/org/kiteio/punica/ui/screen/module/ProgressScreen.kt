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
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import org.kiteio.punica.R
import org.kiteio.punica.datastore.Progresses
import org.kiteio.punica.edu.system.api.ProgressTable
import org.kiteio.punica.edu.system.api.progress
import org.kiteio.punica.getString
import org.kiteio.punica.ui.collectAsEduSystemIdentified
import org.kiteio.punica.ui.component.BottomSheet
import org.kiteio.punica.ui.component.NavBackTopAppBar
import org.kiteio.punica.ui.component.ScaffoldBox
import org.kiteio.punica.ui.component.SubduedText
import org.kiteio.punica.ui.component.Title
import org.kiteio.punica.ui.dp4
import org.kiteio.punica.ui.navigation.Route

/**
 * 学业进度
 */
@Composable
fun ProgressScreen() {
    val progress = Progresses.collectAsEduSystemIdentified { progress() }

    var progressBottomSheetVisible by remember { mutableStateOf(false) }
    var visibleTable by remember { mutableStateOf<ProgressTable?>(null) }

    ScaffoldBox(topBar = { NavBackTopAppBar(route = Route.Module.Progress) }) {
        LazyColumn(contentPadding = PaddingValues(dp4(2))) {
            progress?.run {
                items(tables) {
                    ElevatedCard(
                        onClick = {
                            visibleTable = it
                            progressBottomSheetVisible = true
                        },
                        modifier = Modifier.padding(dp4(2))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(dp4(4)),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Title(text = it.name)
                            Text(
                                text = "${it.point} / ${it.requiredPoint}",
                                color = if (it.point.toDouble() / it.requiredPoint.ifBlank { "0" }
                                        .toDouble() >= 1) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }
    }

    ProgressBottomSheet(
        visible = progressBottomSheetVisible,
        onDismiss = { progressBottomSheetVisible = false },
        table = visibleTable
    )
}


/**
 * 进度详情
 * @param visible
 * @param onDismiss
 * @param table
 */
@Composable
private fun ProgressBottomSheet(visible: Boolean, onDismiss: () -> Unit, table: ProgressTable?) {
    BottomSheet(visible = visible, onDismiss = onDismiss, skipPartiallyExpanded = true) {
        table?.run {
            LazyColumn(contentPadding = PaddingValues(dp4(2))) {
                item {
                    Column(modifier = Modifier.padding(dp4(2))) {
                        Title(text = name)
                        SubduedText(
                            text = "$point / $requiredPoint",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                items(items) {
                    val had = it.point.ifBlank { "0" }
                        .toDouble() / it.requiredPoint.toDouble() == 1.0

                    ElevatedCard(onClick = {}, enabled = had, modifier = Modifier.padding(dp4(2))) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(dp4(4))
                        ) {
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Title(text = it.name)
                                SubduedText(text = getString(R.string.id) + " ${it.id}")
                                SubduedText(text = it.module)
                                SubduedText(text = "${getString(R.string.recommended_term)} ${it.term}")
                                Spacer(modifier = Modifier.height(dp4()))
                                it.privilege?.let { privilege -> SubduedText(text = privilege) }
                            }
                            Spacer(modifier = Modifier.width(dp4(2)))

                            Title(text = it.requiredPoint)
                        }
                    }
                }
            }
        }
    }
}