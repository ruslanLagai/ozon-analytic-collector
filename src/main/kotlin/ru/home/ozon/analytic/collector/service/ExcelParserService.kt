package ru.home.ozon.analytic.collector.service

import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.DataFormatter
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.springframework.stereotype.Service
import java.io.InputStream

@Service
class ExcelParserService {

    private val dataFormatter = DataFormatter()

    fun parse(
        inputStream: InputStream,
        sheetIndex: Int = 0,
    ): List<Map<String, String?>> = inputStream.use { stream ->
        XSSFWorkbook(stream).use { workbook ->
            val sheet = workbook.getSheetAt(sheetIndex)
            val headerRow = sheet.firstOrNull() ?: return emptyList()
            val headers = headerRow.map { cell -> dataFormatter.formatCellValue(cell).trim() }

            sheet.drop(1)
                .filter { row -> !row.isCompletelyBlank() }
                .map { row ->
                    headers.mapIndexed { columnIndex, header ->
                        header to row.getCell(columnIndex)?.toCellValue()
                    }.toMap()
                }
        }
    }

    private fun org.apache.poi.ss.usermodel.Row.isCompletelyBlank(): Boolean =
        this.none { cell ->
            cell.cellType != CellType.BLANK && dataFormatter.formatCellValue(cell).isNotBlank()
        }

    private fun org.apache.poi.ss.usermodel.Cell.toCellValue(): String? {
        val value = dataFormatter.formatCellValue(this).trim()
        return value.ifBlank { null }
    }
}

