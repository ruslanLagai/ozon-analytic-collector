package ru.home.ozon.analytic.collector.dto

data class OrdersWithSPPData(
    val spp: Double,
    val paidByCustomer: Double,
    val sppPercentage: Double,
    val ordered: Int,
    val sku: String,
    val offerId: String,
    val clusterOrders: Map<String, Int>,
    val deliveryPercentage: Double
)