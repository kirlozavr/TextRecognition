package com.example.textrecognition.test

data class Line(
    val text: String,
    val recognizedLanguage: String,
    val bbox: Bbox,
    val listElement: List<Element>
)
