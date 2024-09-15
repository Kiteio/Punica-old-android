package org.kiteio.punica.candy

import kotlinx.coroutines.*
import org.kiteio.punica.Toast
import org.kiteio.punica.datastore.Keys
import org.kiteio.punica.datastore.Preferences


/**
 * [Toast] 层异常，不会被过滤
 * @property message
 */
class ToastLayerException(override val message: String) : Throwable()


/**
 * 抛出 [ToastLayerException]
 * @param message
 * @return [Nothing]
 */
fun errorOnToastLayer(message: String): Nothing = throw ToastLayerException(message)


/**
 * [launch] 一个捕获异常的 [block]
 * @receiver [CoroutineScope]
 * @param onError
 * @param block
 * @return [Job]
 */
fun CoroutineScope.launchCatch(
    onError: (Throwable) -> Unit = {},
    block: suspend CoroutineScope.() -> Unit
) = launch { catchUnit(onError) { block() } }


/**
 * 捕获异常
 * @param onError
 * @param block
 * @return [T]
 */
@OptIn(DelicateCoroutinesApi::class)
inline fun <T> catch(onError: (Throwable) -> T, block: () -> T) = try {
    block()
} catch (e: ToastLayerException) {
    Toast(e.message).show()
    onError(e)
} catch (e: Throwable) {
    GlobalScope.launch(Dispatchers.Main) {
        Preferences.data.collect {
            if (it[Keys.debug] == true) {
                Toast(e.message ?: e.toString()).show()
            }
        }
    }
    onError(e)
}


/**
 * 捕获异常
 * @param block
 * @return [T]?
 */
inline fun <T> catch(block: () -> T) = catch({ null }, block)


/**
 * 捕获异常
 * @param onError
 * @param block
 */
inline fun catchUnit(onError: (Throwable) -> Unit = {}, block: () -> Unit) = catch(onError, block)