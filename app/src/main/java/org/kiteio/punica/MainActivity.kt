package org.kiteio.punica

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import org.kiteio.punica.candy.catching
import org.kiteio.punica.datastore.Keys
import org.kiteio.punica.datastore.Preferences
import org.kiteio.punica.datastore.Users
import org.kiteio.punica.datastore.get
import org.kiteio.punica.edu.foundation.User
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
                val viewModel = viewModel { AppViewModel() }

                // 自动登录
                LaunchedEffect(key1 = Unit) {
                    catching {
                        flow {
                            Preferences.data.collect {
                                it[Keys.lastUsername]?.let { username -> emit(username) }
                            }
                        }.collect { username ->
                            Users.data.collect {
                                it.get<User>(username)?.let { user ->
                                    viewModel.login(user).first()
                                }
                            }
                        }
                    }
                }

                val navController = rememberNavController()

                CompositionLocalProvider(
                    LocalViewModel provides viewModel,
                    LocalNavController provides navController
                ) {
                    NavHost(
                        navController = navController,
                        startRoute = Route.Main,
                        enterTransition = { fadeIn() + slideInHorizontally { it / 2 } },
                        exitTransition = { fadeOut() + slideOutHorizontally { -it / 2 } },
                        popEnterTransition = { fadeIn() + slideInHorizontally { -it / 2 } },
                        popExitTransition = { fadeOut() + slideOutHorizontally { it / 2 } },
                    ) {
                        composable(Route.Main)
                        composable(Route.Login)
                        composable(Route.Account)
                        composable(Route.Version)
                        composable(Route.Settings)
                        composable(Route.Module.values)
                    }
                }
            }
        }
    }
}