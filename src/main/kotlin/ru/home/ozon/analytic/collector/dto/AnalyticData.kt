package ru.home.ozon.analytic.collector.dto

data class AnalyticData(
    var sku: String = "",
    var ctr: Double = 0.0,
    var ordered: Int = 0,
    var conversionFromClickToBasket: Double = 0.0,
    var conversionFromBasketToOrder: Double = 0.0
) {
    fun copy(): AnalyticData {
        return AnalyticData(
            sku = this.sku,
            ctr = this.ctr,
            ordered = this.ordered,
            conversionFromClickToBasket = this.conversionFromClickToBasket,
            conversionFromBasketToOrder = this.conversionFromBasketToOrder
        )
    }
}