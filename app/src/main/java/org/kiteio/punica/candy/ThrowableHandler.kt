package org.kiteio.punica.candy

import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.kiteio.punica.ui.Toast
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
 * 生成 [Toast]
 * @receiver [Throwable]
 * @param duration
 * @return [Toast]
 */
fun Throwable.Toast(duration: Int = Toast.LENGTH_SHORT) = Toast(message ?: toString(), duration)