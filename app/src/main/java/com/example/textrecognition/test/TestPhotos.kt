package com.example.textrecognition.test

import android.content.Context
import com.bumptech.glide.Glide
import com.example.textrecognition.TextModel
import com.google.gson.Gson
import com.google.mlkit.vision.text.Text
import dagger.hilt.android.qualifiers.ApplicationContext
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.system.measureTimeMillis

@Singleton
internal class TestPhotos @Inject constructor(
    @ApplicationContext private val applicationContext: Context,
    private val textModel: TextModel
) {

    internal suspend fun getListExcelEntity(itemsCount: Int): List<ExcelEntity>{

        val resultList = mutableListOf<ExcelEntity>()

        val inputStream = applicationContext.assets.open("tsd_1.xlsx")
        val excelBook = XSSFWorkbook(inputStream)
        val sheet = excelBook.getSheetAt(0)

        val range = 1..itemsCount + 1

        range.forEach { index ->

            val row = sheet.getRow(index)
            val reasonId = row.getCell(3).numericCellValue.toInt()
            val reasonName = row.getCell(4).stringCellValue
            val photoUrl = row.getCell(8).stringCellValue

            val excelEntity = ExcelEntity(
                reasonId = reasonId,
                reasonName = reasonName,
                photoUrl = photoUrl
            )
            resultList.add(excelEntity)
        }

        excelBook.close()

        return resultList
    }

    internal suspend fun getListResult(listExcelEntity: List<ExcelEntity>): Pair<String, List<Result>>{

        val resultList = mutableListOf<Result>()
        val stringBuilder = StringBuilder()

        listExcelEntity.forEach { excelEntity ->

            val bitmap = Glide.with(applicationContext)
                .asBitmap()
                .load(excelEntity.photoUrl)
                .submit()
                .get()

            val text: Text
            val time = measureTimeMillis {
                text = textModel.recognize(bitmap)
            }

            val textEntity = text.toText(
                width = bitmap.width,
                height = bitmap.height
            )
           val result = Result(
               excelEntity = excelEntity,
               measureTimeMillis = time,
               text = textEntity
           )
            resultList.add(result)

            stringBuilder.appendLine("Причина отсутствия: ${excelEntity.reasonName}")
            stringBuilder.appendLine("Код причины отсутствия: ${excelEntity.reasonId}")
            stringBuilder.appendLine("URL фотографии: ${excelEntity.photoUrl}")
            stringBuilder.appendLine("Время распознавания: ${time}")
            stringBuilder.appendLine("Полный текст: ${textEntity.text}")
            stringBuilder.appendLine("Все блоки: ${textEntity.listTextBlock}")
            stringBuilder.appendLine("Все строки: ${textEntity.listTextBlock.flatMap { it.listLine }}")
            stringBuilder.appendLine("Все элементы: ${textEntity.listTextBlock.flatMap { it.listLine.flatMap { it.listElement } }}")
            stringBuilder.appendLine("===================================================================================================")
        }

        return Pair(stringBuilder.toString(), resultList)
    }

    internal fun saveListResult(listResult: List<Result>){
        val gson = Gson()
        val json = gson.toJson(listResult)
        val file = File(applicationContext.filesDir.absolutePath, "json_result.json")
        file.createNewFile()
        file.writeBytes(json.encodeToByteArray())
    }

    internal fun saveListResult(jsonName: String, listResult: List<Result>){
        val gson = Gson()
        val json = gson.toJson(listResult)
        val file = File(applicationContext.filesDir.absolutePath, "${jsonName}.json")
        file.createNewFile()
        file.writeBytes(json.encodeToByteArray())
    }

    internal fun saveString(string: String){
        val file = File(applicationContext.filesDir.absolutePath, "string_result.txt")
        file.createNewFile()
        file.writeBytes(string.encodeToByteArray())
    }
}