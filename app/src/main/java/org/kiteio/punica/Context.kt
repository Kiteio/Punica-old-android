package org.kiteio.punica

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.annotation.ArrayRes
import androidx.annotation.StringRes
import java.io.File

/** 应用上下文 */
lateinit var AppContext: Context

/** 全局 [Context.getFilesDir] */
val FilesDir: File get() = AppContext.filesDir


/**
 * 将 [uri] 保存至 [Context.getFilesDir]/images/[name]
 * @receiver [Context]
 * @param uri
 * @param name
 * @return [String]
 */
fun Context.uriToImages(uri: Uri, name: String): String =
    contentResolver.openInputStream(uri).use { inputStream ->
        val dir = File(filesDir, "images").apply { mkdir() }
        val file = File(dir, name).apply { createNewFile() }
        val outputStream = file.outputStream()
        inputStream?.copyTo(outputStream)

        file.absolutePath
    }


/**
 * [Toast]
 * @param text
 * @param duration
 * @return [Toast]
 */
fun Toast(text: String, duration: Int = Toast.LENGTH_SHORT): Toast =
    Toast.makeText(AppContext, text, duration)


/**
 * [Toast]
 * @param resId
 * @param duration
 * @return [Toast]
 */
fun Toast(@StringRes resId: Int, duration: Int = Toast.LENGTH_SHORT): Toast =
    Toast.makeText(AppContext, resId, duration)


/**
 * 获取字符串资源
 * @param redId
 * @return [String]
 */
fun getString(@StringRes redId: Int) = AppContext.getString(redId)


/**
 * 获取字符串资源
 * @param redId
 * @param formatArgs
 * @return [String]
 */
fun getString(@StringRes redId: Int, vararg formatArgs: Any) =
    AppContext.getString(redId, *formatArgs)


/**
 * 获取字符串数组资源
 * @param resId
 * @return [Array]<[String]>
 */
fun getStringArray(@ArrayRes resId: Int): Array<String> = AppContext.resources.getStringArray(resId)