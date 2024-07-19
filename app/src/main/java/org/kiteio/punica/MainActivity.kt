package org.kiteio.punica

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import org.kiteio.punica.ui.AppViewModel
import org.kiteio.punica.ui.LocalNavController
import org.kiteio.punica.ui.LocalViewModel
import org.kiteio.punica.ui.PunicaTheme
import org.kiteio.punica.ui.navigation.NavHost
import org.kiteio.punica.ui.navigation.Route
import org.kiteio.punica.ui.navigation.composable

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        AppContext = applicationContext
        setContent {
            PunicaTheme {
                val navController = rememberNavController()

                CompositionLocalProvider(
                    LocalViewModel provides viewModel { AppViewModel() },
                    LocalNavController provides navController
                ) {
                    NavHost(navController = navController, startRoute = Route.Main) {
                        composable(Route.Main)
                        composable(Route.Login)
                        composable(Route.Account)
                        composable(Route.Version)
                        composable(Route.Settings)
                        composable(Route.Module.values)
                        composable(Route.NoticeDetail)
                        composable(Route.WebView)
                    }
                }
            }
        }
    }
}