package com.example.todolist.util

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.InputStream

object ImageManager {
    private const val IMAGES_DIR = "task_images"

    fun saveImage(context: Context, imageUri: Uri): String? {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(imageUri)
            inputStream?.use { input ->
                val imagesDir = File(context.filesDir, IMAGES_DIR)
                if (!imagesDir.exists()) {
                    imagesDir.mkdirs()
                }

                val fileName = "task_image_${System.currentTimeMillis()}.jpg"
                val outputFile = File(imagesDir, fileName)

                outputFile.outputStream().use { output ->
                    input.copyTo(output)
                }

                outputFile.absolutePath
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}