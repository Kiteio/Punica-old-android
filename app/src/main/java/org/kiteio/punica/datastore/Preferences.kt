package org.kiteio.punica.datastore

import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

val DefaultJson = Json { ignoreUnknownKeys = true }


/**
 * 使用 [stringPreferencesKey] 获取字符串并转化为 [T]
 * @receiver [Preferences]
 * @param key
 * @return [T]?
 */
inline operator fun <reified T : @Serializable Identified> Preferences.get(key: String) =
    get(stringPreferencesKey(key))?.let { DefaultJson.decodeFromString<T>(it) }


/**
 * 使用 [stringPreferencesKey] 的 [Identified.id] 设置 [value]
 * @receiver [MutablePreferences]
 * @param value
 */
inline fun <reified T: @Serializable Identified> MutablePreferences.set(value: T) =
    set(stringPreferencesKey(value.id), DefaultJson.encodeToString<T>(value))