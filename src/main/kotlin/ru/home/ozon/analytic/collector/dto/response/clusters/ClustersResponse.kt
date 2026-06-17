package ru.home.ozon.analytic.collector.dto.response.clusters

import com.fasterxml.jackson.annotation.JsonProperty

data class ClustersResponse(
    val result: List<ClusterResultDto> = emptyList(),
)

data class ClusterResultDto(
    @JsonProperty("macrolocal_cluster_id") val macrolocalClusterId: Long = 0,
    val data: ClusterDataDto = ClusterDataDto(),
)

data class ClusterDataDto(
    val fulfillments: List<FulfillmentDto> = emptyList(),
    @JsonProperty("macrolocal_cluster") val macrolocalCluster: MacrolocalClusterDto = MacrolocalClusterDto(),
)

data class FulfillmentDto(
    @JsonProperty("warehouse_id") val warehouseId: String = "",
    val name: String = "",
)

data class MacrolocalClusterDto(
    val name: String = "",
    val country: CountryDto = CountryDto(),
)

data class CountryDto(
    val uid: String = "",
    val name: String = "",
)

