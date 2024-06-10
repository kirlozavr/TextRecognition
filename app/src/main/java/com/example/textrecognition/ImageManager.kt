package com.example.textrecognition

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.content.FileProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

internal class ImageManager @Inject constructor(
    @ApplicationContext private val applicationContext: Context
) {

    /**
     * Метод возвращает Uri файла из FileProvider для доступа к файлу из других приложений
     *
     * @param file файл, к которому необходимо предоставить доступ
     * @return Uri с префиксом content:// вместо file://
     *
     * @author Kirlozavr 23.05.2024
     */
    internal fun getUriFromFileProvider(file: File): Uri {
        return FileProvider.getUriForFile(
            applicationContext,
            applicationContext.packageName + PROVIDER_SUFFIX,
            file
        )
    }

    /**
     * Метод создает новый файл для фотографии в директории CASH и возвращает путь
     *
     * @return Абсолютный путь к файлу изображения
     * @author Kirlozavr 22.05.2024
     */
    internal fun getCacheImageFile(): File =
        createImageFile(null, ImageSuffix.JPEG, CACHE_PHOTOS)

    /**
     * Метод чистит директорию CASH от фотографий
     *
     * @return true - если все файлы удалены, false - в противном случае
     * @author Kirlozavr 22.05.2024
     */
    internal fun clearCachePhotos(): Boolean = clearFileFromDirectory(CACHE_PHOTOS)

    /**
     * Метод сохраняет фотографию в директории CACHE_PHOTOS и возвращает путь
     *
     * @param bitmap изображение
     * @return Абсолютный путь к изображению, null если не удалось сохранить
     * @author Kirlozavr 22.05.2024
     */
    internal fun saveCacheImage(bitmap: Bitmap): String? {
        val image = createImageFile(null, ImageSuffix.JPEG, CACHE_PHOTOS)
        val isSaved = saveImageToFile(bitmap, image, QUALITY_100)

        return if (isSaved) {
            image.absolutePath
        } else {
            null
        }
    }

    private fun createImageFile(
        fileName: String?, imageSuffix: ImageSuffix, vararg path: String): File {

        val filename = if (fileName == null) {
            "${System.currentTimeMillis()}${imageSuffix.suffix}"
        } else {
            "$fileName${imageSuffix.suffix}"
        }
        val appDir = applicationContext.filesDir.path
        val fullDir = StringBuilder()
        fullDir.append(appDir)
        path.forEach { fullDir.append(File.separator + it) }

        val directory = File(fullDir.toString())
        if (!directory.exists()) {
            directory.mkdirs()
        }

        val image = File(fullDir.toString(), filename)
        image.createNewFile()

        return image
    }

    private fun saveImageToFile(bitmap: Bitmap, file: File, quality: Int): Boolean {
        return try {
            val out = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out)
            out.flush()
            out.close()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private fun clearFileFromDirectory(vararg path: String): Boolean {
        val filePath = StringBuilder()
        path.forEach { fileName -> filePath.append("/$fileName") }
        val appDir = applicationContext.filesDir.path
        val file = File(appDir + filePath)
        return file.deleteRecursively()
    }

    companion object {
        private const val PROVIDER_SUFFIX = ".provider"
        private const val QUALITY_100 = 100

        private const val CACHE_PHOTOS = "cache_photos"
        private const val TASK_JSONS = "task_jsons"
        private const val EQUIPMENTS_PHOTOS = "equipments_photos"
        private const val BAYS_PHOTOS = "bays_photos"
        private const val STOCK_PHOTOS = "stock_photos"
        private const val LAYOUT_PHOTOS = "layout_photos"
        private const val STORE_ENTER_PHOTOS = "store_enter_photos"
        private const val COMPRESSED_PHOTOS = "compressed_photos"
        private const val CROPS_MODELS_PHOTOS = "crops_models_photos"
    }

    internal enum class ImageSuffix(val suffix: String) {
        JPEG(".jpeg"),
        PNG(".png")
    }
}