package ru.home.ozon.analytic.collector.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.eq
import org.mockito.kotlin.isNull
import org.mockito.kotlin.whenever
import ru.home.ozon.analytic.collector.client.OzonClient
import ru.home.ozon.analytic.collector.dto.response.stocks.GetStocksAnalyticsResponseDto
import ru.home.ozon.analytic.collector.entity.PositionEntity
import ru.home.ozon.analytic.collector.repository.PositionRepository

class OzonServiceTest {
    val ozonClient: OzonClient = mock()
    val parser = OzonPostingReportCsvParser()
    val positionRepository: PositionRepository = mock()

    val service = OzonService(ozonClient, parser, positionRepository)

    val mapper = jacksonObjectMapper()

    @Test
    fun getStocks() {
        runBlocking {
            whenever(positionRepository.findByArchiveIsFalse()).thenReturn(
                listOf(
                    PositionEntity(0L, "3389954573", "", false, ""),
                    PositionEntity(0L, "1134671293", "", false, ""),
                    PositionEntity(0L, "1134715033", "", false, "")

                )
            )
            val json = requireNotNull(javaClass.getResource("/analytic/stocks-analitycs.json")).readText()
            val resp = mapper.readValue(json, GetStocksAnalyticsResponseDto::class.java).items
            whenever(ozonClient.getStocks(isNull(), eq(setOf("3389954573", "1134671293", "1134715033"))))
                .thenReturn(resp)

            val actual = service.getStocks()

            assertEquals(3, actual.size)
            assertEquals(462, actual["1134671293"]!!.total)
            assertEquals("1134671293", actual["1134671293"]!!.sku)
            assertEquals(23, actual["1134671293"]!!.stocksInCluster.size)
            assertEquals(462, actual["1134671293"]!!.stocksInCluster.values.sumOf { it.stock })

            assertEquals("3389954573", actual["3389954573"]!!.sku)
            assertEquals(514, actual["3389954573"]!!.total)
            assertEquals(23, actual["3389954573"]!!.stocksInCluster.size)
            assertEquals(514, actual["3389954573"]!!.stocksInCluster.values.sumOf { it.stock })

            assertEquals("1134715033", actual["1134715033"]!!.sku)
            assertEquals(571, actual["1134715033"]!!.total)
            assertEquals(23, actual["1134715033"]!!.stocksInCluster.size)
            assertEquals(571, actual["1134715033"]!!.stocksInCluster.values.sumOf { it.stock })
        }
    }

}