package org.kiteio.punica.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.serialization.Serializable
import org.kiteio.punica.Preferences
import org.kiteio.punica.candy.catching
import org.kiteio.punica.candy.collectAsState
import org.kiteio.punica.candy.daysUntil
import org.kiteio.punica.candy.launchCatching
import org.kiteio.punica.datastore.DefaultJson
import org.kiteio.punica.datastore.Identified
import org.kiteio.punica.datastore.Keys
import org.kiteio.punica.datastore.get
import org.kiteio.punica.datastore.set
import org.kiteio.punica.edu.foundation.Semester
import org.kiteio.punica.edu.system.EduSystem
import java.time.LocalDate
import kotlin.math.floor

/**
 * [remember] 来自本地或远端的 [EduSystem] 数据，并且自动将远端数据保存至 [DataStore]
 * @receiver [DataStore]<[Preferences]>
 * @param semester 对于区分学期的数据，该参数是必要的
 * @param fromRemote
 * @return [T]?
 */
@Composable
inline fun <reified T : @Serializable Identified> DataStore<Preferences>.rememberIdentified(
    semester: Semester? = null,
    crossinline fromRemote: suspend EduSystem.() -> T
): T? {
    val preferences by Preferences.data.collectAsState()
    val viewModel = LocalViewModel.current
    val dataPreferences by data.collectAsState()
    var value by remember { mutableStateOf<T?>(null) }

    LaunchedEffect(key1 = preferences, key2 = semester) {
        launchCatching {
            // 存储非空 value
            value = viewModel.eduSystem?.run {
                catching<T> { fromRemote() }
            }?.also { value -> edit { it.set(value) } } ?: run {
                preferences?.get(Keys.lastUser)?.let { username ->
                    dataPreferences?.get<T>(username + (semester ?: ""))
                }
            }
        }
    }

    return value
}


/**
 * [remember] 来自远端的 [EduSystem] 列表数据
 * @param fromRemote
 * @return [SnapshotStateList]<[T]>
 */
@Composable
fun <T> rememberRemoteList(fromRemote: suspend EduSystem.() -> List<T>): SnapshotStateList<T> {
    val viewModel = LocalViewModel.current
    val list = remember { mutableStateListOf<T>() }

    LaunchedEffect(key1 = viewModel.eduSystem) {
        launchCatching {
            list.clear()
            viewModel.eduSystem?.run { fromRemote() }?.let {
                list.addAll(it)
            }
        }
    }

    return list
}


/**
 * [remember] 本地 [Identified] 列表
 * @receiver [DataStore]<[Preferences]>
 * @return [SnapshotStateList]<[T]>
 */
@Composable
inline fun <reified T : @Serializable Identified> DataStore<Preferences>.rememberIdentifiedList(): SnapshotStateList<T> {
    val preferences by data.collectAsState()
    val list = remember { mutableStateListOf<T>() }

    LaunchedEffect(key1 = preferences) {
        preferences?.asMap()?.values?.forEach {
            list.add(DefaultJson.decodeFromString<T>(it.toString()))
        }
    }

    return list
}


/**
 * [remember] 根据 [localDate] 生成的周次
 * @param localDate
 * @return [Int]
 */
@Composable
fun rememberWeek(localDate: LocalDate = LocalDate.now()): Int {
    val preferences by Preferences.data.collectAsState()

    val week by remember {
        derivedStateOf {
            preferences?.get(Keys.schoolStart)?.let {
                floor(LocalDate.parse(it).daysUntil(localDate) / 7.0).toInt() + 1
            } ?: 0
        }
    }

    return week
}


/**
 * [remember] 开学日期 [LocalDate]
 * @return [LocalDate]
 */
@Composable
fun rememberSchoolStartDate(): LocalDate {
    val preferences by Preferences.data.collectAsState()

    val schoolStart by remember {
        derivedStateOf {
            preferences?.get(Keys.schoolStart)?.let {
                LocalDate.parse(it)
            } ?: LocalDate.now()
        }
    }

    return schoolStart
}