package ru.home.ozon.analytic.collector.dto.response.stocks

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonSetter
import com.fasterxml.jackson.annotation.Nulls

data class GetStocksAnalyticsItemDto(
    val ads: Double = 0.0,
    @JsonProperty("ads_cluster") val adsCluster: Double = 0.0,
    @JsonProperty("available_stock_count") val availableStockCount: Long = 0,
    @JsonProperty("cluster_id") val clusterId: Long = 0,
    @JsonProperty("cluster_name") val clusterName: String = "",
    @JsonProperty("days_without_sales") val daysWithoutSales: Long = 0,
    @JsonProperty("days_without_sales_cluster") val daysWithoutSalesCluster: Long = 0,
    @JsonProperty("excess_stock_count") val excessStockCount: Long = 0,
    @JsonProperty("expiring_stock_count") val expiringStockCount: Long = 0,
    val idc: Long? = 0,
    @JsonProperty("idc_cluster") val idcCluster: Long? = 0,
    @JsonProperty("item_tags") val itemTags: List<String> = emptyList(),
    @JsonProperty("macrolocal_cluster_id") val macrolocalClusterId: Long = 0,
    val name: String = "",
    @JsonProperty("offer_id") val offerId: String = "",
    @JsonProperty("other_stock_count") val otherStockCount: Long = 0,
    @JsonProperty("requested_stock_count") val requestedStockCount: Long = 0,
    @JsonProperty("return_from_customer_stock_count") val returnFromCustomerStockCount: Long = 0,
    @JsonProperty("return_to_seller_stock_count") val returnToSellerStockCount: Long = 0,
    val sku: Long = 0,
    @JsonProperty("stock_defect_stock_count") val stockDefectStockCount: Long = 0,
    @JsonProperty("transit_defect_stock_count") val transitDefectStockCount: Long = 0,
    @JsonProperty("transit_stock_count") val transitStockCount: Long = 0,
    @JsonProperty("turnover_grade") val turnoverGrade: String = "",
    @JsonProperty("turnover_grade_cluster") val turnoverGradeCluster: String = "",
    @JsonProperty("valid_stock_count") val validStockCount: Long = 0,
    @JsonProperty("waiting_docs_stock_count") val waitingDocsStockCount: Long = 0,
    @JsonProperty("warehouse_id") val warehouseId: Long = 0,
    @JsonProperty("warehouse_name") val warehouseName: String = "",
)


