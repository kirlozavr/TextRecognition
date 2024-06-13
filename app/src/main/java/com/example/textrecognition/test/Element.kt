package com.example.textrecognition.test

data class Element(
    val text: String,
    val recognizedLanguage: String,
    val bbox: Bbox
)
