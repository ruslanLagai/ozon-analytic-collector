package ru.home.ozon.analytic.collector.dto.response.report

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class GetReportInfoReposeTest {

	private val objectMapper = jacksonObjectMapper()

	@Test
	fun `deserializes get report info response`() {
		val json = """
			{
				"result": {
					"code": "REPORT_seller_postings_1224331_1780037942_019e7287-7d42-726d-9bde-8a7bcabbd564",
					"status": "success",
					"error": "",
					"file": "https://ir.ozone.ru/s3/ord-report-service-7/seller_postings_v2/seller_postings_v2-seller-1224331-time-1780037942.csv",
					"report_type": "seller_postings",
					"params": {},
					"created_at": "2026-05-29T06:59:02.594905Z",
					"expires_at": "2026-06-05T06:59:02.594905Z"
				}
			}
		""".trimIndent()

		val response = objectMapper.readValue<GetReportInfoRepose>(json)

		assertEquals("REPORT_seller_postings_1224331_1780037942_019e7287-7d42-726d-9bde-8a7bcabbd564", response.result.code)
		assertEquals("success", response.result.status)
		assertEquals("seller_postings", response.result.reportType)
		assertEquals("2026-05-29T06:59:02.594905Z", response.result.createdAt)
		assertEquals("2026-06-05T06:59:02.594905Z", response.result.expiresAt)
		assertEquals(emptyMap<String, Any?>(), response.result.params)
	}
}

