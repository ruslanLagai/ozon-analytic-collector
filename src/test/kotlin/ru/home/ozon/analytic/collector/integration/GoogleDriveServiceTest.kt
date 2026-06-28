package ru.home.ozon.analytic.collector.integration

import com.google.api.services.drive.Drive
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.TestPropertySources
import org.springframework.test.context.junit.jupiter.SpringExtension
import ru.home.ozon.analytic.collector.config.GoogleDriveConfig
import ru.home.ozon.analytic.collector.config.properties.ServiceProperties
import ru.home.ozon.analytic.collector.dto.AnalyticData
import ru.home.ozon.analytic.collector.dto.MarketingData
import ru.home.ozon.analytic.collector.dto.OrdersWithSPPData
import ru.home.ozon.analytic.collector.dto.StocksData
import ru.home.ozon.analytic.collector.dto.StocksInCluster
import ru.home.ozon.analytic.collector.service.GoogleDriveService
import ru.home.ozon.analytic.collector.util.EnvFileProcessor
import ru.home.ozon.analytic.collector.util.YamlPropertySourceFactory
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [GoogleDriveConfig::class, GoogleDriveServiceTest.TestConfig::class])
@TestPropertySources(
    value = [
        TestPropertySource(locations = ["/application-test.yml"], factory = YamlPropertySourceFactory::class),
        TestPropertySource(locations = ["/.env"], factory = EnvFileProcessor::class)
    ]
)
class GoogleDriveServiceTest {

    @Autowired
    private lateinit var googleDriveService: GoogleDriveService

    @Autowired
    private lateinit var drive: Drive

    @Test
//    @Disabled
    fun `test populate metrics`() {
        val analyticData = mapOf(
            "3389954573" to AnalyticData(sku = "3389954573", ctr = 0.1, ordered = 10, conversionFromClickToBasket = 0.2, conversionFromBasketToOrder = 0.3),
            "3340285564" to AnalyticData(sku = "3340285564", ctr = 0.2, ordered = 20, conversionFromClickToBasket = 0.2, conversionFromBasketToOrder = 0.3)
        )
        val marketingData = listOf(
            MarketingData(drr = 0.5, spent = 100.0, ordered = 2, name = "Зонты", spentPerOrder = 50.0)
        )
        val ordersInCluster = mapOf(
            "Москва, МО и Дальние регионы" to 10,
            "Санкт-Петербург и СЗО" to 2,
            "Беларусь" to 4
        )
        val ordersData = mapOf(
            "3389954573" to OrdersWithSPPData(spp = 1.0, sppPercentage = 0.1, ordered = 5, sku = "3389954573", offerId = "", clusterOrders = ordersInCluster, paidByCustomer = 10.0, deliveryPercentage = 70.0),
            "3340285564" to OrdersWithSPPData(spp = 2.0, sppPercentage = 0.1, ordered = 5, sku = "3340285564", offerId = "", clusterOrders = ordersInCluster, paidByCustomer = 20.0, deliveryPercentage = 80.0)
        )
        val stocksInCluster = mapOf(
            "Москва, МО и Дальние регионы" to StocksInCluster(1L, "", 20),
        )
        val stocksData = mapOf(
            "3389954573" to StocksData(sku = "3389954573", offerId = "", total = 100, name = "", stocksInCluster = stocksInCluster),
            "3340285564" to StocksData(sku = "3340285564", offerId = "", total = 100, name = "", stocksInCluster = stocksInCluster)
        )

        googleDriveService.populateData(analyticData, marketingData, ordersData, stocksData)

    }

    @Test
    fun `getMarketingData parses marketing metrics from exported workbook`() {
        runBlocking {
            val result = googleDriveService.getMarketingData()

            assertEquals(2, result.size)
        }
    }

    @Test
    fun `getAnalyticData parses marketing metrics from exported workbook`() {
        runBlocking {
            val result = googleDriveService.getAnalyticReport()

            assertTrue { result.isNotEmpty() }
        }

    }

    @TestConfiguration
    @EnableConfigurationProperties(ServiceProperties::class)
    class TestConfig {

        @Bean
        fun googleDriveService(drive: Drive, serviceProperties: ServiceProperties) : GoogleDriveService {
            return GoogleDriveService(drive, serviceProperties)
        }
    }
}