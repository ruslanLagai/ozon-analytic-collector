package ru.home.ozon.analytic.collector.dto.response.perf

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ProductsInPromotionInSearchResponseTest {

    private val objectMapper = jacksonObjectMapper()

    @Test
    fun `deserializes products in promotion in search response`() {
        val json = """
            {
                "products": [
                    {
                        "sku": "1591901858",
                        "sourceSku": "0000033",
                        "imageUrl": "https://cdn1.ozone.ru/s3/multimedia-1-k/7049127656.jpg",
                        "title": "Сумка дорожная, сумка спортивная, черная",
                        "price": "2418",
                        "bid": 23,
                        "bidPrice": "556.14",
                        "previousBid": {
                            "bid": 0,
                            "bidPrice": "0.00",
                            "updatedAt": "2026-02-06T12:24:35.865238Z"
                        },
                        "views": {
                            "thisWeek": "325",
                            "previousWeek": "371"
                        },
                        "visibilityIndex": "10+",
                        "previousVisibilityIndex": "10+",
                        "hint": {
                            "campaignId": "5353543",
                            "organisationTitle": "HouseMouse"
                        },
                        "searchPromoStatus": true,
                        "isSearchPromoAvailable": true,
                        "carrotsStatus": "CARROTS_STATUS_DISABLED",
                        "bidWithoutAdditive": 23,
                        "carrotsAdditive": 0
                    }
                ],
                "total": "6"
            }
        """.trimIndent()

        val response = objectMapper.readValue(json, ProductsInPromotionInSearchResponse::class.java)

        assertEquals("6", response.total)
        assertEquals(1, response.products.size)
        assertEquals("1591901858", response.products.first().sku)
        assertEquals("0000033", response.products.first().sourceSku)
        assertEquals(23, response.products.first().bid)
        assertEquals("556.14", response.products.first().bidPrice)
        assertEquals(0, response.products.first().previousBid.bid)
        assertEquals("325", response.products.first().views.thisWeek)
        assertEquals("5353543", response.products.first().hint.campaignId)
        assertTrue(response.products.first().searchPromoStatus)
        assertTrue(response.products.first().isSearchPromoAvailable)
        assertFalse(response.products.first().carrotsAdditive != 0)
    }
}

