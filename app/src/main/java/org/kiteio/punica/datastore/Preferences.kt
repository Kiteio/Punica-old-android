package org.kiteio.punica.datastore

import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.kiteio.punica.candy.catch

val DefaultJson = Json { ignoreUnknownKeys = true }


/**
 * 使用 [stringPreferencesKey] 获取字符串并转化为 [T]
 * @receiver [Preferences]
 * @param key
 * @return [T]?
 */
inline fun <reified T : @Serializable Identified> Preferences.get(key: String) =
    get(stringPreferencesKey(key))?.let { catch { DefaultJson.decodeFromString<T>(it) } }


/**
 * 使用 [stringPreferencesKey] 的 [Identified.id] 设置 [value]
 * @receiver [MutablePreferences]
 * @param value
 */
inline fun <reified T : @Serializable Identified> MutablePreferences.set(value: T) =
    set(stringPreferencesKey(value.id), DefaultJson.encodeToString<T>(value))


/**
 * 移除 [value]
 * @receiver [MutablePreferences]
 * @param value
 */
fun <T: @Serializable Identified> MutablePreferences.remove(value: T) {
    remove(stringPreferencesKey(value.id))
}


/**
 * 返回所有值
 * @receiver [Preferences]
 * @return [List]<[T]?>
 */
inline fun <reified T : @Serializable Identified> Preferences.values(): List<T> = asMap().values.map {
    DefaultJson.decodeFromString<T>(it.toString())
}


/**
 * 返回所有键
 * @receiver [Preferences]
 * @return [List]<[String]>
 */
fun Preferences.keys() = asMap().keys.map { it.name }