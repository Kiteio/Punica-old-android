package org.kiteio.punica.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.kiteio.punica.candy.catching
import org.kiteio.punica.candy.collectAsState
import org.kiteio.punica.candy.launchCatching
import org.kiteio.punica.datastore.Identified
import org.kiteio.punica.datastore.Keys
import org.kiteio.punica.datastore.Preferences
import org.kiteio.punica.datastore.Users
import org.kiteio.punica.datastore.get
import org.kiteio.punica.datastore.set
import org.kiteio.punica.datastore.values
import org.kiteio.punica.edu.foundation.Semester
import org.kiteio.punica.edu.foundation.User
import java.time.LocalDate

/**
 * 本地或远端数据收集为 [Identified]
 * @receiver [DataStore]<[Preferences]>
 * @param id [Identified.id]
 * @param key
 * @param fromRemote
 * @return [T]?
 */
@Composable
inline fun <reified T : @Serializable Identified> DataStore<Preferences>.collectAsIdentified(
    id: String? = rememberLastUsername(),
    key: Any? = null,
    crossinline fromRemote: suspend () -> T?
): T? {
    val coroutineScope = rememberCoroutineScope()
    val preferences by data.collectAsState()
    var value by remember { mutableStateOf<T?>(null) }

    LaunchedEffect(key1 = id, key2 = key) {
        coroutineScope.launch {
            if (id != null) value = catching<T?> {
                fromRemote()
            }?.also { value -> edit { it.set(value) } }

            if (value == null) {
                value = id?.let { preferences?.get<T>(it) }
            }
        }
    }

    return value
}


/**
 * 本地数据收集为 [Identified] 列表
 * @receiver [DataStore]<[Preferences]>
 * @return [SnapshotStateList]<[T]>
 */
@Composable
inline fun <reified T : @Serializable Identified> DataStore<Preferences>.collectAsIdentifiedList(): SnapshotStateList<T> {
    val preferences by data.collectAsState()
    val list = remember { mutableStateListOf<T>() }

    LaunchedEffect(key1 = preferences) {
        catching { preferences?.values<T>()?.let { list.clear(); list.addAll(it) } }
    }

    return list
}


/**
 * 远端数据
 * @param key
 * @param fromRemote
 * @return [T]?
 */
@Composable
fun <T> rememberRemote(key: Any? = null, fromRemote: suspend () -> T?): T? {
    var value by remember { mutableStateOf<T?>(null) }

    LaunchedEffect(key1 = key) {
        value = catching<T?> { fromRemote() }
    }

    return value
}


/**
 * 远端数据列表
 * @param key
 * @param fromRemote
 * @return [SnapshotStateList]<[T]>
 */
@Composable
fun <T> rememberRemoteList(
    key: Any? = null,
    fromRemote: suspend () -> List<T>?
): SnapshotStateList<T> {
    val list = remember { mutableStateListOf<T>() }

    LaunchedEffect(key1 = key) {
        launchCatching {
            list.clear()
            fromRemote()?.let { list.addAll(it) }
        }
    }

    return list
}


/**
 * 返回最近登录的 [User]
 * @return [User]?
 */
@Composable
fun rememberLastUser(): User? = rememberUser(name = rememberLastUsername())


/**
 * 返回 [User.name] 为 [name] 的 [User]
 * @param name
 * @return [User]?
 */
@Composable
fun rememberUser(name: String?): User? {
    val users by Users.data.collectAsState()
    val user by remember(key1 = name, key2 = users) {
        derivedStateOf { name?.let { username -> users?.get<User>(username) } }
    }

    return user
}


/**
 * 返回最近登录 [User.name]
 * @param semester
 * @return [String]?
 */
@Composable
fun rememberLastUsername(semester: Semester? = null): String? {
    val preferences by Preferences.data.collectAsState()
    var username by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(key1 = preferences, key2 = semester) {
        username = preferences?.get(Keys.lastUsername)?.let { it + (semester ?: "") }
    }

    return username
}


/**
 * 如果本地有数据，返回开学日期，否则返回 null
 * @return [LocalDate]
 */
@Composable
fun rememberSchoolStart(): LocalDate? {
    val preferences by Preferences.data.collectAsState()

    val schoolStart by remember {
        derivedStateOf { preferences?.get(Keys.schoolStart)?.let { LocalDate.parse(it) } }
    }

    return schoolStart
}