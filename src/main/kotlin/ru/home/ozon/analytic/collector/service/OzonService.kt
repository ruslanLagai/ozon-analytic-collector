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
        // Stream items from the client and accumulate per SKU without materializing the whole list
        val grouped = mutableMapOf<Long, MutableList<ru.home.ozon.analytic.collector.dto.response.stocks.GetStocksAnalyticsItemDto>>()
        val items = ozonClient.getStocks(clusters = null, skus = skus)
        for (item in items) {
            grouped.computeIfAbsent(item.sku) { mutableListOf() }.add(item)
        }

        return grouped
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
     * СПП + кол-во продаж + процент выкупа
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
                val totalDelivered = report.filter { it.status == "Доставлен" }.size
                val totalCancelled = report
                    .filter { it.status == "Отменён" }
                    .filter { it.handedToDeliveryAt.isBlank() }
                    .size
                val totalOrders = report.filter { it.status != "Отменён" }.size
                val totalInDelivery = report
                    .filter { it.status == "Доставляется" || it.status == "Ожидает сборки" || it.status == "Ожидает отгрузки"}.size

                val percentage = if (totalOrders - totalCancelled - totalInDelivery != 0) {
                    BigDecimal.valueOf(
                        (totalDelivered / (totalOrders - totalCancelled - totalInDelivery)).toDouble() * 100
                    ).setScale(2, RoundingMode.HALF_UP).toDouble()
                } else {
                    Double.NaN
                }

                val ordersInRub = report
                    .filter { it.acceptedAt.toLocalDate() == to.toLocalDate().minusDays(1) }
                    .filter { it.buyerCurrencyCode == "RUB" }
                    .filter { it.status != "Отменён" }
                val orders = report.filter { it.status != "Отменён" }
                    .filter { it.acceptedAt.toLocalDate() == to.toLocalDate().minusDays(1) }
                    .groupBy { it.deliveryCluster }
                    .mapValues { it.value.size }
                val count = report
                    .filter { it.acceptedAt.toLocalDate() == to.toLocalDate().minusDays(1) }
                    .filter { it.status != "Отменён" }
                    .size
                val offerId = report.first().offerId
                if (count == 0) {
                    OrdersWithSPPData(
                        paidByCustomer = 0.0,
                        spp = 0.0,
                        sppPercentage = 0.0,
                        sku = sku,
                        ordered = count,
                        offerId = offerId,
                        clusterOrders = orders,
                        deliveryPercentage = percentage
                    )
                } else {
                    val spp = ordersInRub
                        .filter { it.orderFrom == "Ozon" }
                        .filter { it.yourPrice != null && it.buyerPaidAmount != null }
                        .map { it.yourPrice!! - it.buyerPaidAmount!! }
                        .average()
                        .toBigDecimal()
                        .setScale(2, RoundingMode.HALF_UP)
                        .toDouble()
                    val paidByCustomer = ordersInRub
                        .filter { it.orderFrom == "Ozon" }
                        .filter { it.buyerPaidAmount != null }
                        .map { it.buyerPaidAmount!! }
                        .average()
                        .toBigDecimal()
                        .setScale(2, RoundingMode.HALF_UP)
                        .toDouble()
                    val sppPercentage = ordersInRub
                        .filter { it.orderFrom == "Ozon" }
                        .filter { it.yourPrice != null && it.buyerPaidAmount != null }
                        .map { it.buyerPaidAmount!! / it.yourPrice!! * 100 }
                        .average()
                        .toBigDecimal()
                        .setScale(2, RoundingMode.HALF_UP)
                        .toDouble()
                    OrdersWithSPPData(
                        paidByCustomer = paidByCustomer,
                        spp = spp,
                        sppPercentage = sppPercentage,
                        sku = sku,
                        ordered = count,
                        offerId = offerId,
                        clusterOrders = orders,
                        deliveryPercentage = percentage
                    )
                }
            }

    }
}