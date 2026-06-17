package ru.home.ozon.analytic.collector.dto.response.report

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class CreateReportResponseTest {

    private val objectMapper = jacksonObjectMapper()

    @Test
    fun `deserializes create report response`() {
        val json = """
            {
                "result": {
                    "code": "REPORT_seller_postings_1224331_1780037942_019e7287-7d42-726d-9bde-8a7bcabbd564"
                }
            }
        """.trimIndent()

        val response = objectMapper.readValue<CreateReportResponse>(json)

        assertEquals(
            "REPORT_seller_postings_1224331_1780037942_019e7287-7d42-726d-9bde-8a7bcabbd564",
            response.result.code,
        )
    }
}
