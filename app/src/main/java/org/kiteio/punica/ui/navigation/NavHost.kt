package org.kiteio.punica.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost

private typealias NavAnimateScope = AnimatedContentTransitionScope<NavBackStackEntry>


/**
 * [NavHost]
 * @param navController
 * @param startRoute
 * @param enterTransition
 * @param exitTransition
 * @param popEnterTransition
 * @param popExitTransition
 * @param builder
 */
@Composable
fun NavHost(
    navController: NavHostController,
    startRoute: Route,
    enterTransition: NavAnimateScope.() -> EnterTransition =
        { fadeIn(animationSpec = tween(700)) },
    exitTransition: NavAnimateScope.() -> ExitTransition =
        { fadeOut(animationSpec = tween(700)) },
    popEnterTransition: NavAnimateScope.() -> EnterTransition = enterTransition,
    popExitTransition: NavAnimateScope.() -> ExitTransition = exitTransition,
    builder: NavGraphBuilder.() -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = startRoute.id,
        enterTransition = enterTransition,
        exitTransition = exitTransition,
        popEnterTransition = popEnterTransition,
        popExitTransition = popExitTransition,
        builder = builder
    )
}