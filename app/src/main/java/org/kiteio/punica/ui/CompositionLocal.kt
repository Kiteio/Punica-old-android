package org.kiteio.punica.ui

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavHostController

/** [AppViewModel] */
val LocalViewModel = staticCompositionLocalOf<AppViewModel>()


/** [NavHostController] */
val LocalNavController = staticCompositionLocalOf<NavHostController>()


/**
 * [ProvidableCompositionLocal]
 * @return [ProvidableCompositionLocal]<[T]>
 */
private inline fun <reified T> staticCompositionLocalOf() = staticCompositionLocalOf<T> {
    error("CompositionLocal ${T::class.simpleName} not present")
}