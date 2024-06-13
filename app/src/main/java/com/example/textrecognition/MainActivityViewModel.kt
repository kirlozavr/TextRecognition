package com.example.textrecognition

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.textrecognition.test.Result
import com.example.textrecognition.test.TestPhotos
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.mlkit.vision.text.Text
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
internal class MainActivityViewModel @Inject constructor(
    @ApplicationContext private val applicationContext: Context,
    private val imageManager: ImageManager,
    private val textModel: TextModel,
    private val testPhotos: TestPhotos
) : ViewModel() {

    private val _imageFileFromCameraEvent: SingleLiveEvent<File> = SingleLiveEvent()
    internal val imageFileFromCameraEvent: LiveData<File> get() = _imageFileFromCameraEvent

    private val _textRecognizeEvent: SingleLiveEvent<Pair<Uri, Text>> = SingleLiveEvent()
    internal val textRecognizeEvent: LiveData<Pair<Uri, Text>> get() = _textRecognizeEvent

    private val _finishedEvent: SingleLiveEvent<Unit> = SingleLiveEvent()
    internal val finishedEvent: LiveData<Unit> get() = _finishedEvent

    private val textListener = object : TextModel.TextListener{
        override fun onSuccess(imageUri: Uri, text: Text) {
            _textRecognizeEvent.postValue(Pair(imageUri, text))
        }
    }

    internal fun readFile(){
        val file = File(applicationContext.filesDir.path + "/json_result.json")
        val gson = Gson()
        val listResult = gson.fromJson(file.readText(), object : TypeToken<List<Result>>(){})
        val list_1 = listResult.filter { it.excelEntity.reasonId == 1 }
        val list_2 = listResult.filter { it.excelEntity.reasonId == 2 }
        val list_3 = listResult.filter { it.excelEntity.reasonId == 3 }
        val list_4 = listResult.filter { it.excelEntity.reasonId == 4 }
        val list_5 = listResult.filter { it.excelEntity.reasonId == 5 }
        val list_6 = listResult.filter { it.excelEntity.reasonId == 6 }
        val list_7 = listResult.filter { it.excelEntity.reasonId == 7 }
        testPhotos.saveListResult("json_reason_id_1", list_1)
        testPhotos.saveListResult("json_reason_id_2", list_2)
        testPhotos.saveListResult("json_reason_id_3", list_3)
        testPhotos.saveListResult("json_reason_id_4", list_4)
        testPhotos.saveListResult("json_reason_id_5", list_5)
        testPhotos.saveListResult("json_reason_id_6", list_6)
        testPhotos.saveListResult("json_reason_id_7", list_7)
    }

    internal fun runTest() = viewModelScope.launch(Dispatchers.IO) {
        val listExcelEntity = testPhotos.getListExcelEntity(500)
        val pairResult = testPhotos.getListResult(listExcelEntity)
        testPhotos.saveListResult(pairResult.second)
        testPhotos.saveString(pairResult.first)
        _finishedEvent.postValue(Unit)
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