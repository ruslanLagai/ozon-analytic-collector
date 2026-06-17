package ru.home.ozon.analytic.collector.dto

data class MarketingData(
    val drr: Double,
    val spent: Double,
    val ordered: Int,
    val name: String,
    val spentPerOrder: Double
)