package org.kiteio.punica.ui.navigation

import android.net.Uri
import androidx.navigation.NavHostController

/**
 * 导航到 [route]
 * @receiver [NavHostController]
 * @param route
 */
fun NavHostController.navigate(route: Route, args: List<String>? = null) =
    navigate(route.routeNotArgs + Route.argsOf(args) { Uri.encode(it) })