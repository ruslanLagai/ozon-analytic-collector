package ru.home.ozon.analytic.collector.dto.request.analytic

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate

data class AnalyticRequest(
    @JsonProperty("date_from") val dateFrom: LocalDate,
    @JsonProperty("date_to") val dateTo: LocalDate,
    @JsonProperty("metrics") val metrics: Set<String>,
    @JsonProperty("dimension") val dimension: Set<String>,
    @JsonProperty("filters") val filters: Set<String> = emptySet(),
    @JsonProperty("limit") val limit: Int = 1000,
    @JsonProperty("offset") val offset: Int = 0,
    @JsonProperty("sort") val sort: List<Sorting>? = null
)

data class Sorting(
    val key: String = "",
    val order: String = ""
)