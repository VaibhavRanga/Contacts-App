package com.vaibhavranga.contacts_app.presentation

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.exifinterface.media.ExifInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File

class ImageCompressor(
    val context: Context
) {
    suspend fun compressImage(
        contentUri: Uri,
        file: File
    ): ByteArray? {
        return withContext(Dispatchers.IO) {
            val mimeType = context.contentResolver.getType(contentUri)
            val inputStream = context.contentResolver.openInputStream(contentUri)
            inputStream?.close()
            if (inputStream == null) {
                return@withContext null
            }
            ensureActive()

            withContext(Dispatchers.Default) {

                val compressFormat = when (mimeType) {
                    "image/png" -> Bitmap.CompressFormat.PNG
                    "image/jpeg" -> Bitmap.CompressFormat.JPEG
                    "image/webp" -> if (Build.VERSION.SDK_INT >= 30) {
                        Bitmap.CompressFormat.WEBP_LOSSLESS
                    } else {
                        Bitmap.CompressFormat.WEBP
                    }

                    else -> Bitmap.CompressFormat.JPEG
                }

                val exifInterface = ExifInterface(file.absoluteFile)
                val orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
                )
                ensureActive()
                val bitmap = BitmapFactory.decodeFile(file.absolutePath)

                val matrix = android.graphics.Matrix()
                when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
                    ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
                    ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
                }

                val rotatedBitmap =
                    Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)

                var outputBytes: ByteArray

                ByteArrayOutputStream().use { outputStream ->
                    rotatedBitmap.compress(compressFormat, 20, outputStream)
                    outputBytes = outputStream.toByteArray()
                }
                outputBytes
            }
        }
    }
}