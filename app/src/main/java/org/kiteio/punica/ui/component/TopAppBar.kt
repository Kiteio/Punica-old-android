package org.kiteio.punica.ui.component

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.NavigateBefore
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.kiteio.punica.getString
import org.kiteio.punica.ui.LocalNavController
import org.kiteio.punica.ui.navigation.Route

/**
 * 可回退的 [TopAppBar]
 * @param route
 * @param modifier
 * @param shadowElevation
 * @param actions
 */
@Composable
fun NavBackTopAppBar(
    route: Route,
    modifier: Modifier = Modifier,
    shadowElevation: Dp = 0.8.dp,
    actions: @Composable RowScope.() -> Unit = {}
) {
    NavBackTopAppBar(
        title = { Text(text = getString(route.nameResId)) },
        modifier = modifier,
        shadowElevation = shadowElevation,
        actions = actions
    )
}


/**
 * 可回退的 [TopAppBar]
 * @param title
 * @param modifier
 * @param shadowElevation
 * @param actions
 */
@Composable
fun NavBackTopAppBar(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    shadowElevation: Dp = 0.8.dp,
    actions: @Composable RowScope.() -> Unit = {}
) {
    val navController = LocalNavController.current

    TopAppBar(
        title = title,
        modifier = modifier,
        shadowElevation = shadowElevation,
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
 * @param shadowElevation
 * @param navigationIcon
 * @param actions
 */
@Composable
fun TopAppBar(
    route: Route,
    modifier: Modifier = Modifier,
    shadowElevation: Dp = 0.8.dp,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(
        title = { Text(text = getString(route.nameResId)) },
        modifier = modifier,
        shadowElevation = shadowElevation,
        navigationIcon = navigationIcon,
        actions = actions
    )
}


/**
 * [TopAppBar]
 * @param title
 * @param modifier
 * @param shadowElevation
 * @param navigationIcon
 * @param actions
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    shadowElevation: Dp = 0.8.dp,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {}
) {
    Surface(shadowElevation = shadowElevation) {
        TopAppBar(
            title = title,
            modifier = modifier,
            navigationIcon = navigationIcon,
            actions = actions
        )
    }
}