package org.kiteio.punica.datastore

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

    /** 最后登录学号 */
    val lastUsername by lazy { stringPreferencesKey("lastUsername") }
}