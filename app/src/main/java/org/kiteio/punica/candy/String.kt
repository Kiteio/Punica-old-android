package org.kiteio.punica.candy

import org.json.JSONArray
import org.json.JSONObject

/**
 * [String] 的 [JSONObject] 对象
 */
val String.json get() = JSONObject(this)


/**
 * [String] 的 [JSONArray] 对象
 */
val String.jsonArray get() = JSONArray(this)


/**
 * 限制长度，如果超出 [size] 会使用 [substring] 裁切
 * @receiver [String]
 * @param size
 * @return [String]
 */
fun String.limit(size: Int) = if (length <= size) this else substring(0, size)


/**
 * 如果 [isNotBlank]，返回 [value] 的值，否则返回原值
 * @receiver [String]
 * @param value
 * @return [String]
 */
inline fun String.ifNotBlank(value: (String) -> String) = if (isNotBlank()) value(this) else this