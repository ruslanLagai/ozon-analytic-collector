package ru.home.ozon.analytic.collector.dto.request.report

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.Instant
import java.time.ZonedDateTime

data class CreateReportRequest(
    @JsonProperty("filter") val filter: CreateReportFilterDto,
    @JsonProperty("language") val language: String = "DEFAULT",
    @JsonProperty("with") val with: CreateReportWithDto = CreateReportWithDto(),
)

data class CreateReportFilterDto(
    @JsonProperty("processed_at_from") val processedAtFrom: ZonedDateTime,
    @JsonProperty("processed_at_to") val processedAtTo: ZonedDateTime,
    @JsonProperty("delivery_schema") val deliverySchema: List<String> = listOf("fbo"),
    @JsonProperty("is_express") val isExpress: Boolean? = null,
    @JsonProperty("sku") val sku: List<String> = emptyList(),
    @JsonProperty("cancel_reason_id") val cancelReasonId: List<String> = emptyList(),
    @JsonProperty("offer_id") val offerId: String = "",
    @JsonProperty("status_alias") val statusAlias: List<String> = emptyList(),
    @JsonProperty("statuses") val statuses: List<String> = emptyList(),
    @JsonProperty("title") val title: String = "",
)

data class CreateReportWithDto(
    @JsonProperty("additional_data") val additionalData: Boolean = false,
    @JsonProperty("analytics_data") val analyticsData: Boolean = true,
    @JsonProperty("customer_data") val customerData: Boolean = true,
    @JsonProperty("jewelry_codes") val jewelryCodes: Boolean = false,
)
