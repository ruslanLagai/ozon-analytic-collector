package ru.home.ozon.analytic.collector.dto.request

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.ZonedDateTime

data class Filter (
    @JsonProperty("processed_at_from") val processedAtFrom: ZonedDateTime,
    @JsonProperty("processed_at_to") val processedAtTo: ZonedDateTime,
    @JsonProperty("delivery_schema") val deliverySchema: Set<DeliverySchema>,
    @JsonProperty("is_express") val isExpress: Boolean = true,
    @JsonProperty("sku") val sku: Set<String>,
    @JsonProperty("cancel_reason_id") val cancelReasonId: Set<String> = emptySet(),
    @JsonProperty("offer_id") val offerId: String? = null,
    @JsonProperty("status_alias") val statusAlias: Set<String>? = null,
    @JsonProperty("statuses") val statuses: Set<String>? = null,
    @JsonProperty("title") val title: String? = null,


    ) {
}