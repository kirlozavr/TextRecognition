package com.example.textrecognition

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

internal class TextModel @Inject constructor(
    @ApplicationContext private val applicationContext: Context
) {

    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    internal interface TextListener {
        fun onSuccess(imageUri: Uri, text: Text)
    }

    internal fun recognize(imageUri: Uri, textListener: TextListener) {
        val image = InputImage.fromFilePath(applicationContext, imageUri)
        recognizer.process(image)
            .addOnSuccessListener { text ->
                textListener.onSuccess(imageUri, text)
                recognizer.close()
            }
    }

    internal suspend fun recognize(bitmap: Bitmap): Text{
        return coroutineScope {
            val deferred = async {
                suspendCoroutine<Text> { continuation ->
                    val image = InputImage.fromBitmap(bitmap, 0)
                    recognizer.process(image).addOnSuccessListener {
                        continuation.resume(it)
                    }
                }
            }
            deferred.await()
        }
    }
}