package ru.home.ozon.analytic.collector.dto.request.analytic

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.ZonedDateTime

data class ProductsQueriesRequest(
    @JsonProperty("date_from") val dateFrom: ZonedDateTime,
    @JsonProperty("date_to") val dateTo: ZonedDateTime,
    @JsonProperty("limit_by_sku") val limitBySku: Int = 15,
    @JsonProperty("page") val page: Int = 0,
    @JsonProperty("page_size") val pageSize: Int = 100,
    @JsonProperty("skus") val skus: List<String> = emptyList(),
    @JsonProperty("sort_by") val sortBy: String = "BY_SEARCHES",
    @JsonProperty("sort_dir") val sortDir: String = "DESCENDING",
)