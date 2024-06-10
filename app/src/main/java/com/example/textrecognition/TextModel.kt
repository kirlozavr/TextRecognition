package com.example.textrecognition

import android.content.Context
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

internal class TextModel @Inject constructor(
    @ApplicationContext private val applicationContext: Context
) {

    internal interface TextListener {
        fun onSuccess(imageUri: Uri, text: Text)
    }

    internal fun recognize(imageUri: Uri, textListener: TextListener) {
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        val image = InputImage.fromFilePath(applicationContext, imageUri)
        recognizer.process(image)
            .addOnSuccessListener { text ->
                textListener.onSuccess(imageUri, text)
                recognizer.close()
            }
    }
}