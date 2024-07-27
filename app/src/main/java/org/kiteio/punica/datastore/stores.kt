package org.kiteio.punica.datastore

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import org.kiteio.punica.AppContext

/** 首选项（设置） */
val Preferences get() = AppContext.Preferences
private val Context.Preferences by preferencesDataStore("preferences")

/** 用户 */
val Users get() = AppContext.Users
private val Context.Users by preferencesDataStore("users")

/** 课表 */
val Timetables get() = AppContext.Timetables
private val Context.Timetables by preferencesDataStore("timetables")

/** 考试安排 */
val ExamPlans get() = AppContext.ExamPlans
private val Context.ExamPlans by preferencesDataStore("examPlans")

/** 课程成绩 */
val SchoolReports get() = AppContext.SchoolReports
private val Context.SchoolReports by preferencesDataStore("schoolReports")

/** 等级成绩 */
val LevelReports get() = AppContext.LevelReports
private val Context.LevelReports by preferencesDataStore("levelReports")

/** 第二课堂 */
val SecondClassReports get() = AppContext.SecondClassReports
private val Context.SecondClassReports by preferencesDataStore("secondClassReports")

/** 校园网用户 */
val CampusNetUsers get() = AppContext.CampusNetUsers
private val Context.CampusNetUsers by preferencesDataStore("campusNetUsers")

/** 全校课表 */
val TimetableAlls get() = AppContext.TimetableAlls
private val Context.TimetableAlls by preferencesDataStore("timetableAlls")

/** 执行计划 */
val Plans get() = AppContext.Plans
private val Context.Plans by preferencesDataStore("plans")

/** 学业进度 */
val Progresses get() = AppContext.Progresses
private val Context.Progresses by preferencesDataStore("progresses")

/** 待办 */
val Todos get() = AppContext.Todos
private val Context.Todos by preferencesDataStore("todos")