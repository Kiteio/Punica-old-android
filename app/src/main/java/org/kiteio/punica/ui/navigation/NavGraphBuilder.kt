package org.kiteio.punica.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

/**
 * 使用 [route] 构建 [composable] 路由
 * @receiver [NavGraphBuilder]
 * @param route
 */
fun NavGraphBuilder.composable(route: Route) = composable(route.id) { route.content(it) }


/**
 * 多个 [routes] 构建 [composable] 路由
 * @receiver [NavGraphBuilder]
 * @param routes
 */
fun NavGraphBuilder.composable(routes: List<Route>) = routes.forEach(::composable)