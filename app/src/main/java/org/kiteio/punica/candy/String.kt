package org.kiteio.punica.candy

import org.json.JSONObject

/**
 * [String] 的 [JSONObject] 对象
 */
val String.json get() = JSONObject(this)


/**
 * 限制长度，如果超出 [size] 会使用 [substring] 裁切
 * @receiver [String]
 * @param size
 * @return [String]
 */
fun String.limit(size: Int) = if (length <= size) this else substring(0, size)