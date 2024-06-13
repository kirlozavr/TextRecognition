package com.example.textrecognition

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.example.textrecognition.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
internal class MainActivity : AppCompatActivity() {

    private val viewModel: MainActivityViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding

    private val PERMISSION_CODE = 200
    private val WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE
    private val READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE
    private val READ_MEDIA_IMAGES = Manifest.permission.READ_MEDIA_IMAGES
    private val CAMERA = Manifest.permission.CAMERA

    private var _cameraManager: CameraManager? = null
    private val cameraManager get() = _cameraManager!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        _cameraManager = CameraManager(activityResultRegistry)
        lifecycle.addObserver(cameraManager)

        initViews()
        initObservers()
        viewModel.runTest()

        setContentView(binding.root)
    }

    private fun initObservers(){
        viewModel.finishedEvent.observe(this){
            Toast.makeText(
                applicationContext,
                "Всеапмашмащшкрауркмаркгрмг гркгмркугргр куме",
                Toast.LENGTH_LONG
            ).show()
        }

        viewModel.imageFileFromCameraEvent.observe(this){ imageFile ->
            viewModel.recognize(imageFile.toUri())
        }

        viewModel.textRecognizeEvent.observe(this){ pair ->
            binding.buttonCamera.visibility = View.GONE
            binding.buttonGallery.visibility = View.GONE
            binding.buttonUndo.visibility = View.VISIBLE

            binding.imageViewResult.setImageURI(pair.first)

            val stringBuilder = StringBuilder()

            pair.second.textBlocks.forEachIndexed { index, textBlock ->
                val text = textBlock.text
                stringBuilder.appendLine("block index: $index text: $text")

                textBlock.lines.forEachIndexed { indexLine, line ->
                    val textLine = line.text
                    stringBuilder.appendLine("line index: $indexLine text: $textLine")
                }

                stringBuilder.appendLine("======")
            }


            binding.textViewResult.text = stringBuilder.toString()
        }
    }

    private fun initViews(){
        binding.buttonCamera.setOnClickListener {
            if (checkPermissionCamera()) {
                viewModel.openCamera(cameraManager)
            }
        }

        binding.buttonGallery.setOnClickListener {
            if (checkPermissionStorage()){
                selectFromGallery.launch("image/*")
            }
        }

        binding.buttonUndo.setOnClickListener {
            binding.imageViewResult.setImageURI(null)
            binding.textViewResult.text = ""
            binding.buttonCamera.visibility = View.VISIBLE
            binding.buttonGallery.visibility = View.VISIBLE
            binding.buttonUndo.visibility = View.GONE
        }
    }

    private val selectFromGallery =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uriFromResult ->
            if (uriFromResult == null) return@registerForActivityResult
            viewModel.recognize(uriFromResult)
        }

    private fun checkPermissionStorage(): Boolean {
        return if (Build.VERSION.SDK_INT in 23..32) {
            if (
                checkSelfPermission(READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                    arrayOf(READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE),
                    PERMISSION_CODE
                )
                false
            } else {
                true
            }
        } else if (Build.VERSION.SDK_INT >= 33) {
            if (checkSelfPermission(READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                    arrayOf(READ_MEDIA_IMAGES),
                    PERMISSION_CODE
                )
                false
            } else {
                true
            }
        } else {
            false
        }
    }


    private fun checkPermissionCamera(): Boolean {
        return if (checkSelfPermission(CAMERA) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(CAMERA),
                PERMISSION_CODE
            )
            false
        } else {
            true
        }
    }

}