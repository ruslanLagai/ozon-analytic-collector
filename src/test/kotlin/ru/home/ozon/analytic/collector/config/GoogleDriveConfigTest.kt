package ru.home.ozon.analytic.collector.config

import com.google.api.services.drive.Drive
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.TestPropertySources
import org.springframework.test.context.junit.jupiter.SpringExtension
import ru.home.ozon.analytic.collector.util.EnvFileProcessor
import ru.home.ozon.analytic.collector.util.YamlPropertySourceFactory
import kotlin.test.assertFalse

@ContextConfiguration(classes = [GoogleDriveConfig::class])
@TestPropertySources(
    value = [
        TestPropertySource(locations = ["/application-test.yml"], factory = YamlPropertySourceFactory::class),
        TestPropertySource(locations = ["/.env"], factory = EnvFileProcessor::class)
    ]
)
@ExtendWith(SpringExtension::class)
class GoogleDriveConfigTest {

    @Autowired
    private lateinit var drive: Drive

    @Autowired
    private lateinit var environment: Environment

    @Test
    fun `creates google drive client with bound properties and stub credential`() {
        val result = drive.files().list().execute()
        val stream = drive.files()
            .export("1nftKzr8kagnKwU1_o3-PegKnIMh9F2qNyV29u9nFJ_M", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
            .executeAsInputStream()
        val files = result.files
        assertFalse { files.isEmpty() }
    }
}

