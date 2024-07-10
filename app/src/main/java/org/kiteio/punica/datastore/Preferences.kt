package org.kiteio.punica.datastore

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

val DefaultJson = Json { ignoreUnknownKeys = true }


/**
 * 使用 [stringPreferencesKey] 获取字符串并转化为 [T]
 * @receiver [Preferences]
 * @param key
 * @return [T]?
 */
inline operator fun <reified T: @Serializable Any> Preferences.get(key: String) =
    get(stringPreferencesKey(key))?.let { DefaultJson.decodeFromString<T>(it) }