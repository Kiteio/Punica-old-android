package org.kiteio.punica

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.StringRes
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
import java.io.File

/** 应用上下文 */
private lateinit var AppContext: Context

/** 全局 [Context.getFilesDir] */
val FilesDir: File get() = AppContext.filesDir


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
                    }
                }
            }
        }
    }
}


/**
 * [Toast]
 * @param text
 * @param duration
 * @return [Toast]
 */
fun Toast(text: String, duration: Int = Toast.LENGTH_SHORT): Toast =
    Toast.makeText(AppContext, text, duration)


/**
 * [Toast]
 * @param resId
 * @param duration
 * @return [Toast]
 */
fun Toast(@StringRes resId: Int, duration: Int = Toast.LENGTH_SHORT): Toast =
    Toast.makeText(AppContext, resId, duration)


/**
 * 获取字符串资源
 * @param redId Int
 * @return String
 */
fun getString(@StringRes redId: Int) = AppContext.getString(redId)


/**
 * 获取字符串资源
 * @param redId
 * @param formatArgs
 * @return [String]
 */
fun getString(@StringRes redId: Int, vararg formatArgs: Any) =
    AppContext.getString(redId, formatArgs)