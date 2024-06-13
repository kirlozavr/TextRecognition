package com.example.textrecognition.test

import android.graphics.Rect
import com.google.mlkit.vision.text.Text

fun Text.toText(width: Int, height: Int): com.example.textrecognition.test.Text{
    return Text(
        text = text,
        listTextBlock = textBlocks.map { it.toTextBlock(width, height) }
    )
}

fun Text.TextBlock.toTextBlock(width: Int, height: Int): TextBlock{
    return TextBlock(
        text = text,
        recognizedLanguage = recognizedLanguage,
        bbox = boundingBox!!.toBbox(width, height),
        listLine = lines.map { it.toLine(width, height) }
    )
}

fun Text.Line.toLine(width: Int, height: Int): Line{
    return Line(
        text = text,
        recognizedLanguage = recognizedLanguage,
        bbox = boundingBox!!.toBbox(width, height),
        listElement = elements.map { it.toElement(width, height) }
    )
}

fun Text.Element.toElement(width: Int, height: Int): Element{
    return Element(
        text = text,
        recognizedLanguage = recognizedLanguage,
        bbox = boundingBox!!.toBbox(width, height)
    )
}

fun Rect.toBbox(width: Int, height: Int): Bbox{
    return Bbox(
        left = left.toFloat() / width,
        right = right.toFloat() / width,
        top = top.toFloat() / height,
        bottom = bottom.toFloat() / height
    )
}