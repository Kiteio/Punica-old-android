package org.kiteio.punica.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

fun NavGraphBuilder.composable(route: Route) = composable(route.id) { route.content() }


fun NavGraphBuilder.composable(routes: List<Route>) = routes.forEach(::composable)