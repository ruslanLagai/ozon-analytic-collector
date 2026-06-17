package ru.home.ozon.analytic.collector.dto.request.stocks

import com.fasterxml.jackson.annotation.JsonProperty

data class GetStocksAnalyticsRequest(
    @JsonProperty("cluster_ids") val clusterIds: Set<String>? = emptySet(),
    @JsonProperty("macrolocal_cluster_ids") val macrolocalClusterIds: Set<String> = emptySet(),
    @JsonProperty("skus") val skus: Set<String> = emptySet(),
    @JsonProperty("turnover_grades") val turnoverGrades: List<String>? = null,
    @JsonProperty("warehouse_ids") val warehouseIds: List<String>? = null,
)

