package org.kiteio.punica.candy

import android.util.Log

/**
 * 日志
 */
object Log {
    private const val TAG = "Punica"


    /**
     * Debug 日志
     * @param any
     * @return [Int]
     */
    fun d(vararg any: Any?) = Log.d(TAG, any.joinToString())


    /**
     * Info 日志
     * @param any
     * @return [Int]
     */
    fun i(vararg any: Any?) = Log.i(TAG, any.joinToString())


    /**
     * Error 日志
     * @param any
     * @return [Int]
     */
    fun e(vararg any: Any?) = Log.e(TAG, any.joinToString())
}