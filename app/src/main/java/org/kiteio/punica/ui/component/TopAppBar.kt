package org.kiteio.punica.ui.component

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.NavigateBefore
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.kiteio.punica.getString
import org.kiteio.punica.ui.LocalNavController
import org.kiteio.punica.ui.navigation.Route

/**
 * 可回退的 [TopAppBar]
 * @param route
 * @param modifier
 * @param actions
 */
@Composable
fun NavBackTopAppBar(
    route: Route,
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit = {}
) {
    val navController = LocalNavController.current

    TopAppBar(
        route = route,
        modifier = modifier,
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(imageVector = Icons.AutoMirrored.Rounded.NavigateBefore)
            }
        },
        actions = actions
    )
}


/**
 * [TopAppBar]
 * @param route
 * @param modifier
 * @param navigationIcon
 * @param actions
 */
@Composable
fun TopAppBar(
    route: Route,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {}
) {
    org.kiteio.punica.ui.component.TopAppBar(
        title = { Text(text = getString(route.nameResId)) },
        modifier = modifier,
        navigationIcon = navigationIcon,
        actions = actions
    )
}


/**
 * [TopAppBar]
 * @param title
 * @param modifier
 * @param navigationIcon
 * @param actions
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(
        title = title,
        modifier = modifier,
        navigationIcon = navigationIcon,
        actions = actions
    )
}