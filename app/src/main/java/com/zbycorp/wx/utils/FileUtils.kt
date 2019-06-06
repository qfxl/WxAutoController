package com.zbycorp.wx.utils

import org.apache.poi.hssf.usermodel.HSSFDateUtil
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.FormulaEvaluator
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.text.SimpleDateFormat

object FileUtils {
    fun parseExcel(filePath: String): ArrayList<Pair<String, String>> {
        val contentPairs = ArrayList<Pair<String, String>>()
        try {
            val inputStream = FileInputStream(filePath)
            val workbook = XSSFWorkbook(inputStream)
            val sheet = workbook.getSheetAt(0)
            val rowCount = sheet.physicalNumberOfRows
            var colCount: Int
            val formulaEvaluator = workbook.creationHelper.createFormulaEvaluator()
            //0是sheet 头，不取
            for (r in 1 until rowCount) {
                val row = sheet.getRow(r)
                colCount = row.physicalNumberOfCells
                val name = getCellAsString(row, 0, formulaEvaluator)
                val content = getCellAsString(row, 1, formulaEvaluator)
                contentPairs.add(Pair(name, content))
            }

        } catch (e: FileNotFoundException) {

        } catch (e: IOException) {

        }
        return contentPairs
    }

    private fun getCellAsString(row: Row, c: Int, formulaEvaluator: FormulaEvaluator): String {
        var value = ""
        try {
            val cell = row.getCell(c)
            val cellValue = formulaEvaluator.evaluate(cell)
            when (cellValue.cellType) {
                Cell.CELL_TYPE_BOOLEAN -> value = "" + cellValue.booleanValue
                Cell.CELL_TYPE_NUMERIC -> {
                    val numericValue = cellValue.numberValue
                    value = if (HSSFDateUtil.isCellDateFormatted(cell)) {
                        val date = cellValue.numberValue
                        val formatter = SimpleDateFormat("MM/dd/yy")
                        formatter.format(HSSFDateUtil.getJavaDate(date))
                    } else {
                        "" + numericValue
                    }
                }
                Cell.CELL_TYPE_STRING -> value = "" + cellValue.stringValue
            }
        } catch (e: NullPointerException) {

        }

        return value
    }
}