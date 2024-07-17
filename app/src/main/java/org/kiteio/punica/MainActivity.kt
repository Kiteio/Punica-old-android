package org.kiteio.punica

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.ArrayRes
import androidx.annotation.StringRes
import androidx.compose.runtime.CompositionLocalProvider
import androidx.datastore.preferences.preferencesDataStore
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

private val Context.Preferences by preferencesDataStore("preferences")
private val Context.Users by preferencesDataStore("users")
private val Context.Timetables by preferencesDataStore("timetables")
private val Context.ExamPlans by preferencesDataStore("examPlans")
private val Context.SchoolReports by preferencesDataStore("schoolReports")
private val Context.LevelReports by preferencesDataStore("levelReports")
private val Context.SecondClassReports by preferencesDataStore("secondClassReports")

/** 首选项（设置） */
val Preferences get() = AppContext.Preferences

/** 用户 */
val Users get() = AppContext.Users

/** 课表 */
val Timetables get() = AppContext.Timetables

/** 考试安排 */
val ExamPlans get() = AppContext.ExamPlans

/** 课程成绩 */
val SchoolReports get() = AppContext.SchoolReports

/** 等级成绩 */
val LevelReports get() = AppContext.LevelReports

/** 第二课堂 */
val SecondClassReports get() = AppContext.SecondClassReports


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
 * @param redId
 * @return [String]
 */
fun getString(@StringRes redId: Int) = AppContext.getString(redId)


/**
 * 获取字符串资源
 * @param redId
 * @param formatArgs
 * @return [String]
 */
fun getString(@StringRes redId: Int, vararg formatArgs: Any) =
    AppContext.getString(redId, *formatArgs)


/**
 * 获取字符串数组资源
 * @param resId
 * @return [Array]<[String]>
 */
fun getStringArray(@ArrayRes resId: Int): Array<String> = AppContext.resources.getStringArray(resId)