package ru.home.ozon.analytic.collector.dto.response.perf

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class ProductsInCampaignResponse(
    @JsonProperty("products") val products: List<ProductInCampaignDto> = emptyList(),
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class ProductInCampaignDto(
    @JsonProperty("sku") val sku: String = "",
    @JsonProperty("bid") val bid: String = "",
    @JsonProperty("title") val title: String = "",
    @JsonProperty("targetCir") val targetCir: Int = 0,
)

