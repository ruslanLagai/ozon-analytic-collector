package ru.home.ozon.analytic.collector.dto.response.perf

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ProductsInCampaignResponseTest {

    private val objectMapper = jacksonObjectMapper()

    @Test
    fun `deserializes products in campaign response`() {
        val json = """
            {
                "products": [
                    {
                        "sku": "1134715033",
                        "bid": "0",
                        "title": "зонт механический 8 спиц, мини, бежевый, карманный, складной, маленький",
                        "targetCir": 0
                    }
                ]
            }
        """.trimIndent()

        val response = objectMapper.readValue(json, ProductsInCampaignResponse::class.java)

        assertEquals(1, response.products.size)
        assertEquals("1134715033", response.products.first().sku)
        assertEquals("0", response.products.first().bid)
        assertEquals(
            "зонт механический 8 спиц, мини, бежевый, карманный, складной, маленький",
            response.products.first().title,
        )
        assertEquals(0, response.products.first().targetCir)
    }
}

