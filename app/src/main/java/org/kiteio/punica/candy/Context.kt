package org.kiteio.punica.candy

import android.content.Context
import android.net.Uri
import java.io.File

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