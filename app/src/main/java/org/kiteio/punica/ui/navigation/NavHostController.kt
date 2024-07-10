package org.kiteio.punica.ui.navigation

import androidx.navigation.NavHostController
import androidx.navigation.Navigator

/**
 * 导航到 [route]
 * @receiver [NavHostController]
 * @param route
 * @param navigatorExtras
 */
fun NavHostController.navigate(route: Route, navigatorExtras: Navigator.Extras? = null) =
    navigate(route.id, navigatorExtras = navigatorExtras)