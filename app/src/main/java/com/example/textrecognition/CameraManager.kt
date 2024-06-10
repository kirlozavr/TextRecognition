package com.example.textrecognition

import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

internal class CameraManager constructor(
    private val registry: ActivityResultRegistry
) : DefaultLifecycleObserver {

    private var mTakePicture: ActivityResultLauncher<Uri>? = null
    private var mTakePictureListener: TakePictureListener? = null

    override fun onCreate(owner: LifecycleOwner) = initTakePicture(owner)

    internal fun openCamera(imageUri: Uri, takePictureListener: TakePictureListener) {
        mTakePictureListener = takePictureListener
        mTakePicture?.launch(imageUri)
    }

    private fun initTakePicture(owner: LifecycleOwner) {
        if (mTakePicture != null) return

        mTakePicture = registry.register(
            TAKE_PICTURE_KEY, owner, ActivityResultContracts.TakePicture()
        ) { result -> mTakePictureListener?.onTakePictureResult(result) }
    }

    companion object {
        private const val TAKE_PICTURE_KEY = "TAKE_PICTURE_KEY"
    }

    internal interface TakePictureListener {
        fun onTakePictureResult(result: Boolean)
    }
}