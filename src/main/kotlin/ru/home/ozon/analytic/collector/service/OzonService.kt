package ru.home.ozon.analytic.collector.service

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.home.ozon.analytic.collector.client.OzonClient
import ru.home.ozon.analytic.collector.dto.OrdersWithSPPData
import ru.home.ozon.analytic.collector.dto.StocksData
import ru.home.ozon.analytic.collector.dto.StocksInCluster
import ru.home.ozon.analytic.collector.dto.response.analytic.ProductsQueryDto
import ru.home.ozon.analytic.collector.repository.PositionRepository
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.ZonedDateTime
import kotlin.time.Duration.Companion.milliseconds

@Service
class OzonService(
    private val ozonClient: OzonClient,
    private val ozonPostingReportCsvParser: OzonPostingReportCsvParser,
    private val positionRepository: PositionRepository
) {

    private val log = LoggerFactory.getLogger(OzonService::class.java)

    /**
     * Остатки по кластерам
     */
    suspend fun getStocks() : Map<String, StocksData> {
        val skus = withContext(Dispatchers.IO) {
            positionRepository.findByArchiveIsFalse()
        }
            .map { it.sku }
            .toSet()
        val stocks = ozonClient.getStocks(clusters = null, skus = skus)
        return stocks
            .groupBy { it.sku }
            .map { (sku, stocksAnalytic) ->
                val offerId = stocksAnalytic.first().offerId
                val name = stocksAnalytic.first().name
                val total = stocksAnalytic.sumOf { it.availableStockCount }.toInt()
                val stocksInCluster = stocksAnalytic.groupBy { it.clusterName }
                    .mapValues { (clusterName, stocks) ->
                        StocksInCluster(
                            clusterId = stocks.first().clusterId,
                            clusterName = clusterName,
                            stock = stocks.sumOf { it.availableStockCount }.toInt()
                        )
                    }

                StocksData(
                    sku = sku.toString(),
                    offerId = offerId,
                    total = total,
                    name = name,
                    stocksInCluster = stocksInCluster
                )
            }
            .associateBy { it.sku }
    }


    fun getProductQueries(from: ZonedDateTime, to: ZonedDateTime, skus: List<String>): List<ProductsQueryDto> =
        ozonClient.productQueries(from, to, skus)

    /**
     * СПП + кол-во продаж
     */
    suspend fun getPostingReport(from: ZonedDateTime, to: ZonedDateTime): Map<String, OrdersWithSPPData> {
        val reportCode = ozonClient.createReport(from, to)
        delay(2000.milliseconds)
        val fileUrl = ozonClient.getReportInfo(reportCode)
        log.info("Получен URL для скачивания отчета: $fileUrl")
        val csv = ozonClient.downloadReportCsv(fileUrl)
        return ozonPostingReportCsvParser.parse(csv)
            .groupBy { it.sku }
            .mapValues { (sku, report) ->
                val ordersInRub = report.filter { it.buyerCurrencyCode == "RUB" }.filter { it.status != "Отменён" }
                val orders = report.filter { it.status != "Отменён" }
                    .groupBy { it.deliveryCluster }
                    .mapValues { it.value.size }
                val count = ordersInRub.size
                val offerId = report.first().offerId
                if (count == 0) {
                    OrdersWithSPPData(
                        spp = 0.0,
                        sppPercentage = 0.0,
                        sku = sku,
                        ordered = count,
                        offerId = offerId,
                        clusterOrders = orders
                    )
                } else {
                    var spp = ordersInRub.filter { it.yourPrice != null && it.buyerPaidAmount != null }
                        .sumOf { it.yourPrice!! - it.buyerPaidAmount!! }
                    spp = BigDecimal(spp / count).setScale(2, RoundingMode.HALF_UP).toDouble()
                    val price = ordersInRub.filter { it.yourPrice != null }.sumOf { it.yourPrice!! }
                    val paid = ordersInRub.filter { it.buyerPaidAmount != null }.sumOf { it.buyerPaidAmount!! }
                    val sppPercentage = BigDecimal(paid / price).setScale(2, RoundingMode.HALF_UP).toDouble()
                    OrdersWithSPPData(
                        spp = spp,
                        sppPercentage = sppPercentage,
                        sku = sku,
                        ordered = count,
                        offerId = offerId,
                        clusterOrders = orders
                    )
                }
            }

    }
}