package org.kiteio.punica.ui.navigation

import androidx.navigation.NavHostController

/**
 * 导航到 [route]
 * @receiver [NavHostController]
 * @param route
 */
fun NavHostController.navigate(route: Route) = navigate(route.id)