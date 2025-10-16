package com.sportmaster.surelykmp.repository

import android.content.Context
import android.net.Uri
import com.sportmaster.surelykmp.activities.register.presentation.viewmodels.ImageRepository
import java.io.IOException

class ImageRepositoryImpl(private val context: Context) : ImageRepository {
    override suspend fun readImageBytes(imagePath: String): ByteArray? {
        return try {
            val uri = Uri.parse(imagePath)
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                inputStream.readBytes()
            }
        } catch (e: IOException) {
            e.printStackTrace()
            null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}