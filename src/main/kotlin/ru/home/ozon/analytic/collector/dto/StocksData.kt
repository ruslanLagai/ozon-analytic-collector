package ru.home.ozon.analytic.collector.dto

data class StocksData(
    val sku: String,
    val offerId: String,
    val total: Int,
    val name: String,
    val stocksInCluster: Map<String, StocksInCluster>
)

data class StocksInCluster(
    val clusterId: Long,
    val clusterName: String,
    val stock: Int
)