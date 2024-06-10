package com.example.textrecognition

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.google.mlkit.vision.text.Text
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
import javax.inject.Inject

@HiltViewModel
internal class MainActivityViewModel @Inject constructor(
    private val imageManager: ImageManager,
    private val textModel: TextModel
) : ViewModel() {

    private val _imageFileFromCameraEvent: SingleLiveEvent<File> = SingleLiveEvent()
    internal val imageFileFromCameraEvent: LiveData<File> get() = _imageFileFromCameraEvent

    private val _textRecognizeEvent: SingleLiveEvent<Pair<Uri, Text>> = SingleLiveEvent()
    internal val textRecognizeEvent: LiveData<Pair<Uri, Text>> get() = _textRecognizeEvent

    private val textListener = object : TextModel.TextListener{
        override fun onSuccess(imageUri: Uri, text: Text) {
            _textRecognizeEvent.postValue(Pair(imageUri, text))
        }
    }

    internal fun openCamera(cameraManager: CameraManager) {
        val imageFile = imageManager.getCacheImageFile()
        val imageUri = imageManager.getUriFromFileProvider(imageFile)
        val takePictureListener = object : CameraManager.TakePictureListener {
            override fun onTakePictureResult(result: Boolean) {
                if (!result) return

                _imageFileFromCameraEvent.postValue(imageFile)
            }
        }

        cameraManager.openCamera(imageUri, takePictureListener)
    }

    internal fun recognize(imageUri: Uri){
        textModel.recognize(imageUri, textListener)
    }
}