package org.kiteio.punica.datastore

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

/**
 * 首选项 keys
 */
object Keys {
    /** 头像 Uri，可以是网络地址或本地地址 */
    val avatarUri by lazy { stringPreferencesKey("avatarUri") }

    /** 开学日期 */
    val schoolStart by lazy { stringPreferencesKey("schoolStart") }

    /** 校区 id */
    val campusId by lazy { intPreferencesKey("campusId") }

    /** 课表显示其他周次 */
    val showOtherWeeks by lazy { booleanPreferencesKey("showOtherWeeks") }

    /** 最后登录学号 */
    val lastUsername by lazy { stringPreferencesKey("lastUsername") }

    /** 主题色来源 */
    val themeColorSource by lazy { intPreferencesKey("themeColorSource") }

    /** 自定义主题色 */
    val themeColor by lazy { stringPreferencesKey("themeColor") }

    /** 主题风格 */
    val themeStyle by lazy { intPreferencesKey("themeStyle") }

    /** 调试 */
    val debug by lazy { booleanPreferencesKey("debug") }
}