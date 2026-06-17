package ru.home.ozon.analytic.collector.service

import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

class ExcelParserServiceTest {

    private val excelParserService = ExcelParserService()

    @Test
    fun `parses xlsx from input stream`() {
        val workbookBytes = ByteArrayOutputStream().use { outputStream ->
            XSSFWorkbook().use { workbook ->
                val sheet = workbook.createSheet("Sheet1")
                val header = sheet.createRow(0)
                header.createCell(0).setCellValue("sku")
                header.createCell(1).setCellValue("title")
                header.createCell(2).setCellValue("price")

                val row1 = sheet.createRow(1)
                row1.createCell(0).setCellValue("1134715033")
                row1.createCell(1).setCellValue("Зонт")
                row1.createCell(2).setCellValue("1980")

                val row2 = sheet.createRow(2)
                row2.createCell(0).setCellValue("1591901858")
                row2.createCell(1).setCellValue("Сумка дорожная")

                workbook.write(outputStream)
            }
            outputStream.toByteArray()
        }

        val rows = excelParserService.parse(ByteArrayInputStream(workbookBytes))

        assertEquals(2, rows.size)
        assertEquals("1134715033", rows[0]["sku"])
        assertEquals("Зонт", rows[0]["title"])
        assertEquals("1980", rows[0]["price"])
        assertEquals("1591901858", rows[1]["sku"])
        assertEquals("Сумка дорожная", rows[1]["title"])
        assertNull(rows[1]["price"])
    }
}

