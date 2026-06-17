package ru.home.ozon.analytic.collector.dto.response.analytic

import com.fasterxml.jackson.annotation.JsonProperty

data class ProductsQueriesResponse(
    @JsonProperty("analytics_period") val analyticsPeriod: AnalyticsPeriodDto? = AnalyticsPeriodDto(),
    @JsonProperty("queries") val queries: List<ProductsQueryDto>? = emptyList(),
    @JsonProperty("total") val total: Long? = 0,
    @JsonProperty("page_count") val pageCount: Long? = 0,
    @JsonProperty("message") val message: String? = null,
    @JsonProperty("code") val code: String? = null,

)

data class AnalyticsPeriodDto(
    @JsonProperty("date_from") val dateFrom: String = "",
    @JsonProperty("date_to") val dateTo: String = "",
)

data class ProductsQueryDto(
    @JsonProperty("sku") val sku: Long = 0,
    @JsonProperty("currency") val currency: String = "",
    @JsonProperty("gmv") val gmv: Long = 0,
    @JsonProperty("order_count") val orderCount: Long = 0,
    @JsonProperty("position") val position: Long = 0,
    @JsonProperty("query") val query: String = "",
    @JsonProperty("view_conversion") val viewConversion: Double = 0.0,
    @JsonProperty("query_index") val queryIndex: Long = 0,
    @JsonProperty("unique_search_users") val uniqueSearchUsers: Long = 0,
    @JsonProperty("unique_view_users") val uniqueViewUsers: Long = 0,
)

