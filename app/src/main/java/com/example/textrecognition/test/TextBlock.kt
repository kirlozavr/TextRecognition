package com.example.textrecognition.test

data class TextBlock(
    val text: String,
    val recognizedLanguage: String,
    val bbox: Bbox,
    val listLine: List<Line>
)
