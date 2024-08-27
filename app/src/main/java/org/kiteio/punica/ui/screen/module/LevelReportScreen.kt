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
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ElevatedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.kiteio.punica.datastore.LevelReports
import org.kiteio.punica.edu.system.api.levelReport
import org.kiteio.punica.ui.collectAsEduSystemIdentified
import org.kiteio.punica.ui.component.NavBackTopAppBar
import org.kiteio.punica.ui.component.ScaffoldBox
import org.kiteio.punica.ui.component.SubduedText
import org.kiteio.punica.ui.component.Title
import org.kiteio.punica.ui.dp4
import org.kiteio.punica.ui.navigation.Route

/**
 * 等级成绩
 */
@Composable
fun LevelReportScreen() {
    val levelReport = LevelReports.collectAsEduSystemIdentified { levelReport() }

    ScaffoldBox(topBar = { NavBackTopAppBar(route = Route.Module.LevelReport) }) {
        LazyColumn(contentPadding = PaddingValues(dp4(2))) {
            levelReport?.run {
                items(items) {
                    ElevatedCard(onClick = {}, modifier = Modifier.padding(dp4(2))) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(dp4(4)),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Title(text = it.name)
                                Spacer(modifier = Modifier.height(dp4()))
                                SubduedText(text = it.time)
                            }
                            Title(text = it.score)
                        }
                    }
                }
            }
        }
    }
}