package ru.home.ozon.analytic.collector.scheduled

import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import ru.home.ozon.analytic.collector.service.GoogleDriveService
import ru.home.ozon.analytic.collector.service.OzonService
import java.lang.Thread.yield
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlin.coroutines.EmptyCoroutineContext

@Service
class AnalyticCollector(
    private val ozonService: OzonService,
    private val googleDriveService: GoogleDriveService
) {

    @Scheduled(cron = "\${service.collector.analytic.cron}")
    fun collectData() {
        runBlocking {
            val from = LocalDate.now().minusDays(2).atStartOfDay(ZoneId.of("UTC"))
            val to = LocalDate.now().minusDays(1).atStartOfDay(ZoneId.of("UTC"))

            val report = async(context = Dispatchers.Default, start = CoroutineStart.DEFAULT) {
                ozonService.getPostingReport(from = from, to = to)
            }
            val stocks = async { ozonService.getStocks() }
            val marketing = async { googleDriveService.getMarketingData() }
            val analytic = async { googleDriveService.getAnalyticReport() }

            googleDriveService.populateData(
                analyticData = analytic.await(),
                marketingData = marketing.await(),
                ordersData = report.await(),
                stocksData = stocks.await()
            )

        }

    }
}