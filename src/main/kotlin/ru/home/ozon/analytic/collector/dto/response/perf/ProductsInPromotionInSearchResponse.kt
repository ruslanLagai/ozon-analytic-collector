package ru.home.ozon.analytic.collector.dto.response.perf

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class ProductsInPromotionInSearchResponse(
    @JsonProperty("products") val products: List<ProductInPromotionInSearchDto> = emptyList(),
    @JsonProperty("total") val total: String = "0",
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class ProductInPromotionInSearchDto(
    @JsonProperty("sku") val sku: String = "",
    @JsonProperty("sourceSku") val sourceSku: String = "",
    @JsonProperty("imageUrl") val imageUrl: String = "",
    @JsonProperty("title") val title: String = "",
    @JsonProperty("price") val price: String = "",
    @JsonProperty("bid") val bid: Int = 0,
    @JsonProperty("bidPrice") val bidPrice: String = "",
    @JsonProperty("previousBid") val previousBid: ProductPromotionPreviousBidDto = ProductPromotionPreviousBidDto(),
    @JsonProperty("views") val views: ProductPromotionViewsDto = ProductPromotionViewsDto(),
    @JsonProperty("visibilityIndex") val visibilityIndex: String = "",
    @JsonProperty("previousVisibilityIndex") val previousVisibilityIndex: String = "",
    @JsonProperty("hint") val hint: ProductPromotionHintDto = ProductPromotionHintDto(),
    @JsonProperty("searchPromoStatus") val searchPromoStatus: Boolean = false,
    @JsonProperty("isSearchPromoAvailable") val isSearchPromoAvailable: Boolean = false,
    @JsonProperty("carrotsStatus") val carrotsStatus: String = "",
    @JsonProperty("bidWithoutAdditive") val bidWithoutAdditive: Int = 0,
    @JsonProperty("carrotsAdditive") val carrotsAdditive: Int = 0,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class ProductPromotionPreviousBidDto(
    @JsonProperty("bid") val bid: Int = 0,
    @JsonProperty("bidPrice") val bidPrice: String = "",
    @JsonProperty("updatedAt") val updatedAt: String = "",
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class ProductPromotionViewsDto(
    @JsonProperty("thisWeek") val thisWeek: String = "",
    @JsonProperty("previousWeek") val previousWeek: String = "",
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class ProductPromotionHintDto(
    @JsonProperty("campaignId") val campaignId: String = "",
    @JsonProperty("organisationTitle") val organisationTitle: String = "",
)

