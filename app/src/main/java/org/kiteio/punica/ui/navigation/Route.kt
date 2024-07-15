package org.kiteio.punica.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccessTime
import androidx.compose.material.icons.rounded.AccountBox
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material.icons.rounded.Checklist
import androidx.compose.material.icons.rounded.NewReleases
import androidx.compose.material.icons.rounded.Numbers
import androidx.compose.material.icons.rounded.SentimentSatisfied
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavBackStackEntry
import compose.icons.TablerIcons
import compose.icons.tablericons.BellRinging
import compose.icons.tablericons.Book
import compose.icons.tablericons.Building
import compose.icons.tablericons.ChartLine
import compose.icons.tablericons.Click
import compose.icons.tablericons.Clipboard
import compose.icons.tablericons.FileAnalytics
import compose.icons.tablericons.Id
import compose.icons.tablericons.Paperclip
import compose.icons.tablericons.Pencil
import compose.icons.tablericons.Rocket
import compose.icons.tablericons.Sailboat
import compose.icons.tablericons.Wifi
import compose.icons.tablericons.Windmill
import org.kiteio.punica.R
import org.kiteio.punica.ui.screen.AccountScreen
import org.kiteio.punica.ui.screen.LoginScreen
import org.kiteio.punica.ui.screen.MainScreen
import org.kiteio.punica.ui.screen.SettingsScreen
import org.kiteio.punica.ui.screen.VersionScreen
import org.kiteio.punica.ui.screen.bottom.MeScreen
import org.kiteio.punica.ui.screen.bottom.ModuleScreen
import org.kiteio.punica.ui.screen.bottom.ScheduleScreen
import org.kiteio.punica.ui.screen.bottom.TodoScreen
import org.kiteio.punica.ui.screen.module.CETScreen
import org.kiteio.punica.ui.screen.module.CampusNetScreen
import org.kiteio.punica.ui.screen.module.CourseSystemScreen
import org.kiteio.punica.ui.screen.module.EmergencyCallsScreen
import org.kiteio.punica.ui.screen.module.EvaluationScreen
import org.kiteio.punica.ui.screen.module.ExamPlanScreen
import org.kiteio.punica.ui.screen.module.LevelReportScreen
import org.kiteio.punica.ui.screen.module.NotEmptyRoomScreen
import org.kiteio.punica.ui.screen.module.NoticeScreen
import org.kiteio.punica.ui.screen.module.PlanScreen
import org.kiteio.punica.ui.screen.module.ProgressScreen
import org.kiteio.punica.ui.screen.module.SchoolReportScreen
import org.kiteio.punica.ui.screen.module.SecondClassScreen
import org.kiteio.punica.ui.screen.module.TeacherInfoScreen
import org.kiteio.punica.ui.screen.module.TimetableAllScreen

/**
 * 路由
 * @property content 页面内容
 * @property nameResId 标题
 * @property icon 图标
 * @property id 唯一标识
 */
sealed class Route(
    val content: @Composable NavBackStackEntry.() -> Unit,
    @StringRes val nameResId: Int = R.string.app_name,
    val icon: ImageVector = Icons.Rounded.Numbers
) {
    val id = this::class.simpleName!!


    /** 主页面 */
    data object Main : Route({ MainScreen() })

    /** 登录页 */
    data object Login : Route({ LoginScreen() })

    /** 账号 */
    data object Account : Route({ AccountScreen() }, R.string.account, Icons.Rounded.AccountBox)

    /** 版本 */
    data object Version : Route({ VersionScreen() }, R.string.version, Icons.Rounded.NewReleases)

    /** 设置 */
    data object Settings : Route({ SettingsScreen() }, R.string.settings, Icons.Rounded.Settings)


    /** 底部导航路由 */
    sealed class Bottom(
        composable: @Composable NavBackStackEntry.() -> Unit,
        @StringRes nameResId: Int,
        icon: ImageVector
    ) : Route(composable, nameResId, icon) {
        /** 日程 */
        data object Schedule :
            Bottom({ ScheduleScreen() }, R.string.schedule, Icons.Rounded.WbSunny)

        /** 待办 */
        data object Todo : Bottom({ TodoScreen() }, R.string.todo, Icons.Rounded.Checklist)

        /** 模块 */
        data object Module : Bottom({ ModuleScreen() }, R.string.module, TablerIcons.Sailboat)

        /** 我 */
        data object Me : Bottom({ MeScreen() }, R.string.me, Icons.Rounded.SentimentSatisfied)


        companion object {
            /** [Bottom] 所有值 */
            val values by lazy {
                listOf(Schedule, Todo, Module, Me)
            }
        }
    }


    /** 功能模块 */
    sealed class Module(
        composable: @Composable NavBackStackEntry.() -> Unit,
        @StringRes nameResId: Int,
        icon: ImageVector
    ) : Route(composable, nameResId, icon) {
        /** 紧急电话 */
        data object EmergencyCalls :
            Module({ EmergencyCallsScreen() }, R.string.emergency_call, Icons.Rounded.Call)

        /** 教学通知 */
        data object Notice : Module({ NoticeScreen() }, R.string.notice, TablerIcons.BellRinging)

        /** 教学评价 */
        data object Evaluation :
            Module({ EvaluationScreen() }, R.string.evaluation, TablerIcons.Pencil)

        /** 选课系统 */
        data object CourseSystem :
            Module({ CourseSystemScreen() }, R.string.course_system, TablerIcons.Click)

        /** 考试安排 */
        data object ExamPlan :
            Module({ ExamPlanScreen() }, R.string.exam_plan, Icons.Rounded.AccessTime)

        /** 四六级考试 */
        data object CET : Module({ CETScreen() }, R.string.cet, TablerIcons.Paperclip)

        /** 课程成绩 */
        data object SchoolReport :
            Module({ SchoolReportScreen() }, R.string.school_report, TablerIcons.Clipboard)

        /** 等级成绩 */
        data object LevelReport :
            Module({ LevelReportScreen() }, R.string.level_report, TablerIcons.FileAnalytics)

        /** 第二课堂 */
        data object SecondClass :
            Module({ SecondClassScreen() }, R.string.second_class, TablerIcons.Windmill)

        /** 教师信息 */
        data object TeacherInfo :
            Module({ TeacherInfoScreen() }, R.string.teacher_info, TablerIcons.Id)

        /** 校园网 */
        data object CampusNet : Module({ CampusNetScreen() }, R.string.campus_net, TablerIcons.Wifi)

        /** 非空教室 */
        data object NotEmptyRoom :
            Module({ NotEmptyRoomScreen() }, R.string.not_empty_room, TablerIcons.Building)

        /** 全校课表 */
        data object TimetableAll :
            Module({ TimetableAllScreen() }, R.string.timetable_all, TablerIcons.Book)

        /** 执行计划 */
        data object Plan : Module({ PlanScreen() }, R.string.plan, TablerIcons.Rocket)

        /** 学业进度 */
        data object Progress :
            Module({ ProgressScreen() }, R.string.progress, TablerIcons.ChartLine)


        companion object {
            /** [Module] 所有值 */
            val values by lazy {
                listOf(
                    EmergencyCalls,
                    Notice,
                    Evaluation,
                    CourseSystem,
                    ExamPlan,
                    CET,
                    SchoolReport,
                    LevelReport,
                    SecondClass,
                    TeacherInfo,
                    CampusNet,
                    NotEmptyRoom,
                    TimetableAll,
                    Plan,
                    Progress
                )
            }
        }
    }
}