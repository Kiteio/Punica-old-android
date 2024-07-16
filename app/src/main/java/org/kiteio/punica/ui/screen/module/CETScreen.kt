package org.kiteio.punica.ui.screen.module

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.rajat.pdfviewer.compose.PdfRendererViewCompose
import org.kiteio.punica.candy.route
import org.kiteio.punica.edu.CET
import org.kiteio.punica.ui.component.NavBackTopAppBar
import org.kiteio.punica.ui.component.ScaffoldColumn
import org.kiteio.punica.ui.component.Title
import org.kiteio.punica.ui.dp4
import org.kiteio.punica.ui.navigation.Route
import org.kiteio.punica.ui.rememberRemote

/**
 * 四六级考试
 */
@Composable
fun CETScreen() {
    val cetTime = rememberRemote { CET.time() }

    ScaffoldColumn(
        topBar = { NavBackTopAppBar(route = Route.Module.CET) },
        innerModifier = Modifier.padding(dp4(2)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        cetTime?.run {
            ElevatedCard(onClick = {}, modifier = Modifier.padding(dp4(2))) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(dp4(4))
                ) {
                    Title(text = name)
                    rows.forEach {
                        Text(text = it, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
            ElevatedCard(modifier = Modifier.padding(dp4(2))) {
                PdfRendererViewCompose(url = CET.route { TIME_PDF })
            }
        }
    }
}