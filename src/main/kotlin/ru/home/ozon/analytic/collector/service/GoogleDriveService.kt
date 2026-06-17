package ru.home.ozon.analytic.collector.service

import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File
import org.apache.poi.ss.usermodel.DataFormatter
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.springframework.stereotype.Service
import ru.home.ozon.analytic.collector.config.properties.ServiceProperties
import ru.home.ozon.analytic.collector.dto.AnalyticData
import ru.home.ozon.analytic.collector.dto.MarketingData
import ru.home.ozon.analytic.collector.dto.OrdersWithSPPData
import ru.home.ozon.analytic.collector.dto.StocksData
import java.time.LocalDate
import java.time.temporal.ChronoField
import java.io.ByteArrayOutputStream
import com.google.api.client.http.ByteArrayContent
import org.apache.poi.ss.usermodel.BorderStyle
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.util.CellRangeAddress
import org.apache.poi.ss.util.RegionUtil
import java.util.LinkedList

@Service
class GoogleDriveService(
    private val drive: Drive,
    private val serviceProperties: ServiceProperties
) {

    private val monthName = mapOf(
        1 to "Январь",
        2 to "Февраль",
        3 to "Март",
        4 to "Апрель",
        5 to "Май",
        6 to "Июнь",
        7 to "Июль",
        8 to "Август",
        9 to "Сентябрь",
        10 to "Октябрь",
        11 to "Ноябрь",
        12 to "Декабрь"
    )

    /**
     * запись данных на диск
     */
    fun populateData(
        analyticData: Map<String, AnalyticData>,
        marketingData: List<MarketingData>,
        ordersData: Map<String, OrdersWithSPPData>,
        stocksData: Map<String, StocksData>) {

        val current = LocalDate.now()
        val month = current.month.get(ChronoField.MONTH_OF_YEAR)
        val year = current.year
        val formatter = DataFormatter()

        // ID файла в Drive
        val fileId = serviceProperties.googleDriveFiles.output

        // Экспортируем Google Spreadsheet в бинарный XLSX
        val inputStream = drive.files()
            .export(fileId, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
            .executeAsInputStream()

        // Открываем workbook, вносим изменения и сохраняем в байтовый массив
        val bytes = XSSFWorkbook(inputStream).use { workbook ->
            val sheet = workbook.getSheet(monthName[month] + " " + year)!!
            val claudeSheet = workbook.getSheet("claude")!!
            val prevDayCol = getColumn(workbook)
            val skus = LinkedList<String>()

            for (i in 1..sheet.lastRowNum step 1) {
                val value = sheet.getRow(i)?.getCell(0)?.let { cell ->
                    val formatter = DataFormatter()
                    formatter.formatCellValue(cell).trim()
                }
                if (!value.isNullOrEmpty()) {
                    val sku = value.split(",")[0].trim()
                    skus.add(sku)
                }
            }

            var rowNum = 1
            for (sku in skus) {
                val analytic = analyticData[sku]
                val orders = ordersData[sku]
                val stocks = stocksData[sku]
                val marketing = marketingData.find { serviceProperties.marketing.skuToGroup[sku] == it.name }

                // analytic
                var row = sheet.getRow(rowNum)
                row.createCell(prevDayCol).cellType = CellType.NUMERIC
                row.getCell(prevDayCol).setCellValue(orders!!.ordered.toDouble())

                row = sheet.getRow(rowNum + 1)
                row.createCell(prevDayCol).cellType = CellType.NUMERIC
                row.getCell(prevDayCol).setCellValue(orders.spp)

                row = sheet.getRow(rowNum + 2)
                row.createCell(prevDayCol).cellType = CellType.NUMERIC
                row.getCell(prevDayCol).setCellValue(orders.sppPercentage)

                // marketing
                row = sheet.getRow(rowNum + 3)
                row.createCell(prevDayCol).cellType = CellType.NUMERIC
                row.getCell(prevDayCol).setCellValue(marketing!!.drr)

                row = sheet.getRow(rowNum + 4)
                row.createCell(prevDayCol).cellType = CellType.NUMERIC
                row.getCell(prevDayCol).setCellValue(marketing.spentPerOrder)

                // analytic
                row = sheet.getRow(rowNum + 5)
                row.createCell(prevDayCol).cellType = CellType.NUMERIC
                row.getCell(prevDayCol).setCellValue(analytic!!.ctr)

                row = sheet.getRow(rowNum + 6)
                row.createCell(prevDayCol).cellType = CellType.NUMERIC
                row.getCell(prevDayCol).setCellValue(analytic.conversionFromClickToBasket)

                row = sheet.getRow(rowNum + 7)
                row.createCell(prevDayCol).cellType = CellType.NUMERIC
                row.getCell(prevDayCol).setCellValue(analytic.conversionFromBasketToOrder)

                val category = serviceProperties.analytic.skuToCategory[sku]
                val categoryRowNum = when (category) {
                    "Зонт" -> 0
                    "Сумка" -> 1
                    else -> throw IllegalArgumentException("Неизвестная категория для SKU: $sku")
                }
                val trend = formatter.formatCellValue(claudeSheet.getRow(categoryRowNum).getCell(1)).replace(",", ".").toDouble()
                row = sheet.getRow(rowNum + 8)
                row.createCell(prevDayCol).cellType = CellType.NUMERIC
                row.getCell(prevDayCol).setCellValue(trend)

                // stocks
                for (i in rowNum + 9..rowNum + 31) {
                    val cluster = formatter.formatCellValue(sheet.getRow(i).getCell(4)).trim()
                    val leftInCluster = stocks!!.stocksInCluster[cluster]?.stock ?: 0.0
                    row = sheet.getRow(i)
                    row.createCell(prevDayCol).cellType = CellType.NUMERIC
                    row.getCell(prevDayCol).setCellValue(leftInCluster.toDouble())
                }
                rowNum += 31

                // orders by cluster
                for (i in rowNum + 1..rowNum + 23) {
                    val cluster = formatter.formatCellValue(sheet.getRow(i).getCell(4)).trim()
                    val ordersInCluster = orders.clusterOrders[cluster] ?: 0
                    row = sheet.getRow(i)
                    row.createCell(prevDayCol).cellType = CellType.NUMERIC
                    row.getCell(prevDayCol).setCellValue(ordersInCluster.toDouble())
                    row.outlineLevel
                }
                rowNum += 24
                RegionUtil.setBorderTop(BorderStyle.DOUBLE, CellRangeAddress(rowNum, rowNum + 1, 0, prevDayCol), sheet)
            }

            val baos = ByteArrayOutputStream()
            workbook.write(baos)
            baos.flush()
            baos.toByteArray()
        }

        // Формируем медиа-контент и обновляем файл
        val mediaContent = ByteArrayContent(
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            bytes
        )

        drive.files().update(fileId, File(), mediaContent).execute()

    }


    /**
     * поулчение метрик по рекламе
     */
    suspend fun getMarketingData() : List<MarketingData> {
        val inputStream = drive.files()
            .export(serviceProperties.googleDriveFiles.marketing, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
            .executeAsInputStream()
        val current = LocalDate.now()
        val month = current.month.get(ChronoField.MONTH_OF_YEAR)
        val year = current.year
        return XSSFWorkbook(inputStream).use { workbook ->
            val sheet = workbook.getSheet(monthName[month] + " " + year)!!
            val prevDayCol = getColumn(workbook)
            serviceProperties.marketing.positions.map { entry ->
                val item = entry.key
                val position = entry.value
                val formatter = DataFormatter()
                MarketingData(
                    drr = formatter.formatCellValue(sheet.getRow(position)!!.getCell(prevDayCol)).replace(",", ".").trim().toDouble(),
                    spent = formatter.formatCellValue(sheet.getRow(position - 7)!!.getCell(prevDayCol)).replace(",", ".").trim().toDouble(),
                    ordered = formatter.formatCellValue(sheet.getRow(position - 6)!!.getCell(prevDayCol)).replace(",", ".").trim().toInt(),
                    name = item,
                    spentPerOrder = formatter.formatCellValue(sheet.getRow(position - 1)!!.getCell(prevDayCol)).replace(",", ".").trim().toDouble()
                )
            }.toList()
        }
    }

    /**
     * Поулчение данных по аналитике
     */
    suspend fun getAnalyticReport() : Map<String, AnalyticData> {
        val inputStream = drive.files()
            .export(serviceProperties.googleDriveFiles.analytic, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
            .executeAsInputStream()
        val current = LocalDate.now()
        val month = current.month.get(ChronoField.MONTH_OF_YEAR)
        val year = current.year
        val result = mutableListOf<AnalyticData>()
        XSSFWorkbook(inputStream).use { workbook ->
            val sheet = workbook.getSheet(monthName[month] + " " + year)!!
            val prevDayCol = getColumn(workbook)
            val formatter = DataFormatter()
            val analyticData = AnalyticData()

            for (rowNum in 1..sheet.lastRowNum) {
                val row = sheet.getRow(rowNum)
                when (formatter.formatCellValue(row.getCell(2))) {
                    "CTR" -> analyticData.ctr = formatter.formatCellValue(row.getCell(prevDayCol)).replace(",", ".").toDouble()
                    "Конверсия в корзину из клика" -> {
                        analyticData.conversionFromClickToBasket = formatter.formatCellValue(row.getCell(prevDayCol)).replace(",", ".").toDouble()
                        result.add(analyticData.copy())
                    }
                    "Конверсия в заказ из корзины" -> analyticData.conversionFromBasketToOrder = formatter.formatCellValue(row.getCell(prevDayCol)).replace(",", ".").toDouble()
                    "Заказано товаров" -> analyticData.ordered = formatter.formatCellValue(row.getCell(prevDayCol)).replace(",", ".").toDouble().toInt()
                }
                if (formatter.formatCellValue(row.getCell(0)).isNotEmpty()) {
                    analyticData.sku = formatter.formatCellValue(row.getCell(0)).trim()
                }
            }
        }
        return result.filter { it.sku.isNotEmpty() }.associateBy { it.sku }
    }

    fun getColumn(workbook: XSSFWorkbook) : Int {
        val current = LocalDate.now()
        val month = current.month.get(ChronoField.MONTH_OF_YEAR)
        val year = current.year
        val day = current.dayOfMonth - 1
        val sheet = workbook.getSheet(monthName[month] + " " + year)!!
        val headerRow = sheet.first()
        val headers = headerRow
            .find { cell ->
                val formatter = DataFormatter()
                val value = formatter.formatCellValue(cell).trim()
                value.split(",")[0].removePrefix("0") == day.toString()
                        || value.split(".")[0].removePrefix("0") == day.toString()
            }!!
        return headers.columnIndex
    }
}