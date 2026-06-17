package ru.home.ozon.analytic.collector.integration

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.TestPropertySources
import org.springframework.test.context.jdbc.Sql
import ru.home.ozon.analytic.collector.service.OzonService
import ru.home.ozon.analytic.collector.util.EnvFileProcessor
import ru.home.ozon.analytic.collector.util.YamlPropertySourceFactory
import java.time.LocalDate
import java.time.ZoneOffset
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@TestPropertySources(
    value = [
        TestPropertySource(locations = ["/application-test.yml"], factory = YamlPropertySourceFactory::class),
        TestPropertySource(locations = ["/.env"], factory = EnvFileProcessor::class)
    ]
)
@SpringBootTest
@Sql(scripts = ["/sql/init.sql"], executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
class OzonServiceIntegrationTest {

    @Autowired
    private lateinit var ozonService: OzonService

    @Test
    fun getPostingReport() {
        runBlocking {
            val from  = LocalDate.now().minusDays(1).atStartOfDay(ZoneOffset.UTC)
            val to = LocalDate.now().atStartOfDay(ZoneOffset.UTC)
            val result = ozonService.getPostingReport(from = from, to = to)

            Assertions.assertTrue { result.isNotEmpty() }
        }
    }

    @Test
    fun getStocks() {
        runBlocking {
            val stocks = ozonService.getStocks()

            assertTrue { stocks.isNotEmpty() }
            assertEquals(4, stocks.size)
        }
    }

}