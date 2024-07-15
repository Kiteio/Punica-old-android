package org.kiteio.punica.ui.screen.bottom

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.kiteio.punica.getString
import org.kiteio.punica.ui.LocalNavController
import org.kiteio.punica.ui.component.Icon
import org.kiteio.punica.ui.component.ScaffoldColumn
import org.kiteio.punica.ui.component.Title
import org.kiteio.punica.ui.dp4
import org.kiteio.punica.ui.navigation.Route
import org.kiteio.punica.ui.navigation.navigate

/**
 * 模块
 */
@Composable
fun ModuleScreen() {
    val navController = LocalNavController.current

    ScaffoldColumn {
        LazyVerticalGrid(columns = GridCells.Fixed(2), contentPadding = PaddingValues(dp4(2))) {
            itemsIndexed(Route.Module.values) { index, item ->
                Module(
                    route = item,
                    onClick = { navController.navigate(item) },
                    modifier = Modifier.padding(dp4(2))
                )
            }
        }
    }
}


/**
 * 模块
 * @param route
 * @param onClick
 * @param modifier
 */
@Composable
private fun Module(route: Route, onClick: () -> Unit, modifier: Modifier = Modifier) {
    ElevatedCard(onClick = onClick, modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = dp4())
        ) {
            Card(onClick = onClick) {
                Icon(imageVector = route.icon, modifier = Modifier.padding(dp4()))
            }
            Column {
                Title(text = getString(route.nameResId))
            }
        }
    }
}