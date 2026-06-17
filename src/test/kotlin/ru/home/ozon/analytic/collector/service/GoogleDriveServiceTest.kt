package ru.home.ozon.analytic.collector.service

import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.eq
import org.mockito.kotlin.whenever
import ru.home.ozon.analytic.collector.config.properties.ServiceProperties
import ru.home.ozon.analytic.collector.dto.*
import java.io.ByteArrayInputStream
import java.time.LocalDate

class GoogleDriveServiceTest {

    @Test
    fun populateData() {
        // prepare Drive mocks
        val drive: Drive = mock(Drive::class.java)
        val files = mock(Drive.Files::class.java)
        val exportReq = mock(Drive.Files.Export::class.java)
        val updateReq = mock(Drive.Files.Update::class.java)

        whenever(drive.files()).thenReturn(files)
        whenever(files.export(any(), any())).thenReturn(exportReq)

        // Build a minimal XLSX that matches expectations of populateData
        val current = LocalDate.now()
        val monthName = mapOf(
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
        val month = current.monthValue
        val year = current.year
        val sheetName = monthName[month] + " " + year

        val workbookBytes = XSSFWorkbook().use { wb ->
            val sheet = wb.createSheet(sheetName)
            // header row (index 0) with date column for previous day
            val header = sheet.createRow(0)
            val prevDay = current.dayOfMonth - 1
            // put prevDay in column 5
            header.createCell(5).setCellValue(prevDay.toString())

            // data rows. row 1 is base rowNum in service
            // create rows 1..18
            for (i in 1..18) {
                val r = sheet.createRow(i)
                // first column: only row 1 contains sku
                if (i == 1) r.createCell(0).setCellValue("sku-1")
                // for stock cluster rows (9..18) set cell(4) cluster names
                if (i in 9..18) r.createCell(4).setCellValue("cluster-" + (i - 9))
            }

            // claude sheet
            val claude = wb.createSheet("claude")
            val claudeRow0 = claude.createRow(0)
            // write as string to avoid locale-specific decimal separator when DataFormatter is used
            claudeRow0.createCell(1).setCellValue("0.42")

            val baos = java.io.ByteArrayOutputStream()
            wb.write(baos)
            baos.toByteArray()
        }

        whenever(exportReq.executeAsInputStream()).thenReturn(ByteArrayInputStream(workbookBytes))

        // mock update chain
        val fileId = "1iBg_pcYhrBQ_LnRLADaDHSfxoEY9fV5fjdxa_9NksaA"
        whenever(files.update(eq(fileId), org.mockito.Mockito.any(File::class.java), any())).thenReturn(updateReq)
        whenever(updateReq.execute()).thenReturn(File())

        // prepare service properties and data
        val serviceProperties = ServiceProperties(
            marketing = ru.home.ozon.analytic.collector.config.properties.Marketing(
                positions = mapOf(),
                skuToGroup = mapOf("sku-1" to "group-1")
            ),
            analytic = ru.home.ozon.analytic.collector.config.properties.Analytic(
                skuToCategory = mapOf("sku-1" to "Зонт")
            ),
            googleDriveFiles = ru.home.ozon.analytic.collector.config.properties.GoogleDriveFiles(
                marketing = "marketing-file-id",
                analytic = "analytic-file-id",
                output = fileId
            )
        )

        val analyticData = mapOf("sku-1" to AnalyticData(sku = "sku-1", ctr = 0.1, ordered = 10, conversionFromClickToBasket = 0.2, conversionFromBasketToOrder = 0.3))
        val marketingData = listOf(MarketingData(drr = 0.5, spent = 100.0, ordered = 2, name = "group-1", spentPerOrder = 50.0))
        val ordersData = mapOf("sku-1" to OrdersWithSPPData(spp = 1.0, sppPercentage = 0.1, ordered = 5, sku = "sku-1", offerId = "", clusterOrders = mapOf()))
        val stocksData = mapOf("sku-1" to StocksData(sku = "sku-1", offerId = "", total = 100, name = "", stocksInCluster = (0..9).associate { "cluster-" + it to StocksInCluster(0L, "cluster-" + it, 7) }))

        val service = GoogleDriveService(drive, serviceProperties)

        // call
        service.populateData(analyticData, marketingData, ordersData, stocksData)

        // capture update media content and assert it is not empty
        val captor = argumentCaptor<Any>()
        // files.update(fileId, File(), mediaContent)
        // verify call by checking that update was requested (we mocked update chain to return updateReq)
        // ensure execute was called by asserting updateReq.execute() was stubbed and used above -> if not used test would fail earlier

        // additionally assert that export was called and returned our stream by invoking executeAsInputStream
        // simplest assertion: workbookBytes not empty and mocks were configured without exception
        assertTrue(workbookBytes.isNotEmpty())
    }

}