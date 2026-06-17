package ru.home.ozon.analytic.collector.dto.response.perf

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test

class GetCampaignsResponseTest {

    private val objectMapper = jacksonObjectMapper()

    @Test
    fun `deserializes campaigns response`() {
        val json = """
            {
                "list": [
                    {
                        "id": "28059441",
                        "title": "коврик",
                        "state": "CAMPAIGN_STATE_RUNNING",
                        "advObjectType": "SKU",
                        "fromDate": "2026-05-27",
                        "toDate": "",
                        "dailyBudget": "0",
                        "placement": ["PLACEMENT_TOP_PROMOTION"],
                        "budget": "0",
                        "createdAt": "2026-05-27T09:09:26.577884Z",
                        "updatedAt": "2026-05-27T09:09:26.577884Z",
                        "productCampaignMode": "PRODUCT_CAMPAIGN_MODE_AUTO",
                        "productAutopilotStrategy": "TARGET_BIDS",
                        "autopilot": null,
                        "PaymentType": "CPC",
                        "expenseStrategy": "DAILY_BUDGET",
                        "weeklyBudget": "7000000000",
                        "budgetType": "PRODUCT_CAMPAIGN_BUDGET_TYPE_WEEKLY",
                        "startWeekDay": "WEDNESDAY",
                        "endWeekDay": "TUESDAY",
                        "autoIncrease": null,
                        "ProductAdvPlacements": [],
                        "isAutocreated": false,
                        "autostopStatus": "AUTOSTOP_STATUS_NONE"
                    }
                ],
                "total": "9"
            }
        """.trimIndent()

        val response = objectMapper.readValue(json, GetCampaignsResponse::class.java)

        assertEquals("9", response.total)
        assertEquals(1, response.list.size)
        assertEquals("28059441", response.list.first().id)
        assertEquals("CPC", response.list.first().paymentType)
        assertEquals("SKU", response.list.first().advObjectType)
        assertEquals("PRODUCT_CAMPAIGN_MODE_AUTO", response.list.first().productCampaignMode)
        assertFalse(response.list.first().isAutocreated)
        assertEquals(emptyList<Any>(), response.list.first().productAdvPlacements)
    }
}

