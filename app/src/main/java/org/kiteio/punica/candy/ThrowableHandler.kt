package org.kiteio.punica.candy

import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.kiteio.punica.Toast
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * [launch] 一个捕获异常的 [block]
 * @receiver [CoroutineScope]
 * @param context
 * @param onCatch 捕获异常回调
 * @param start
 * @param block
 * @return [Job]
 */
inline fun CoroutineScope.launchCatching(
    context: CoroutineContext = EmptyCoroutineContext,
    crossinline onCatch: Throwable.() -> Unit = { Toast(message ?: toString()).show() },
    start: CoroutineStart = CoroutineStart.DEFAULT,
    crossinline block: suspend CoroutineScope.() -> Unit
) = launch(context, start) { catching(onCatch) { block() } }


/**
 * 开启一个捕获异常的 [block]
 * @param onCatch 捕获异常回调
 * @param block
 */
inline fun catching(
    onCatch: Throwable.() -> Unit = { Toast().show() },
    block: () -> Unit
) = try {
    block()
} catch (e: Throwable) {
    onCatch(e)
}


/**
 * 开启一个捕获异常的 [block]，在无异常时返回 [block] 返回的值，否则为 null
 * @param onCatch 捕获异常回调
 * @param block
 * @return [R]?
 */
inline fun <R> catching(
    onCatch: Throwable.() -> Unit = { Toast().show() },
    block: () -> R
) = try {
    block()
}catch (e: Throwable) {
    onCatch(e)
    null
}


/**
 * 生成 [Toast]
 * @receiver [Throwable]
 * @param duration
 * @return [Toast]
 */
fun Throwable.Toast(duration: Int = Toast.LENGTH_SHORT) = Toast(message ?: toString(), duration)