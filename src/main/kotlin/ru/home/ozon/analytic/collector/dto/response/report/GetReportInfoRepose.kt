package ru.home.ozon.analytic.collector.dto.response.report

import com.fasterxml.jackson.annotation.JsonProperty

data class GetReportInfoRepose(
    val result: GetReportInfoReposeResultDto = GetReportInfoReposeResultDto(),
)

data class GetReportInfoReposeResultDto(
    val code: String = "",
    val status: String = "",
    val error: String = "",
    val file: String = "",
    @JsonProperty("report_type") val reportType: String = "",
    val params: Map<String, Any?> = emptyMap(),
    @JsonProperty("created_at") val createdAt: String = "",
    @JsonProperty("expires_at") val expiresAt: String = "",
)
