package org.kiteio.punica.candy

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapFactory.Options
import android.graphics.Color
import com.googlecode.tesseract.android.TessBaseAPI
import io.ktor.client.statement.readRawBytes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.kiteio.punica.FilesDir
import org.kiteio.punica.request.fetch
import java.io.File

/**
 * OCR
 * @receiver [ByteArray]
 * @return [String]
 */
suspend fun ByteArray.text() = withContext(Dispatchers.Default) {
    with(TessBaseAPI()) {
        val filesDir = FilesDir.absolutePath
        val fileDir = "$filesDir/tessdata/"
        val dataFile = File(fileDir + "eng.traineddata")

        // 数据文件不存在
        if (!dataFile.exists()) {
            // 下载
            val byteArray = fetch(URLs.TESSDATA).readRawBytes()
            // 创建并写入文件
            File(fileDir).mkdir()
            withContext(Dispatchers.IO) {
                dataFile.createNewFile()
                dataFile.outputStream().write(byteArray)
            }
        }

        init(filesDir, "eng")

        // 图像转化处理
        bitmap().apply {
            val border = 3
            for (y in 0..<height) for (x in 0..<width) {
                if (x < border || x > width - border || y < border || y > height - border)
                // 去除边框
                    setPixel(x, y, Color.WHITE)
                else {
                    // 二值化
                    val pixel = getPixel(x, y)
                    val red = Color.red(pixel)
                    val green = Color.green(pixel)
                    val blue = Color.blue(pixel)
                    setPixel(x, y, if ((red + green + blue) / 3 < 113) Color.BLACK else Color.WHITE)
                }
            }
            // 向 TessBaseAPI 设置 Bitmap
            setImage(this@apply)
        }

        return@withContext utF8Text.filter { it.isLetterOrDigit() }.also { recycle() }
    }
}


/**
 * 创建 [Bitmap]
 * @receiver [ByteArray]
 * @param offset
 * @param length
 * @return [Bitmap]
 */
fun ByteArray.bitmap(offset: Int = 0, length: Int = size): Bitmap =
    BitmapFactory.decodeByteArray(this, offset, length, Options().apply { inMutable = true })