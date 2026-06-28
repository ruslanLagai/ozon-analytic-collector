package ru.home.ozon.analytic.collector.dto.ozon.report

import java.time.LocalDateTime

/**
 * Строка CSV-отчета Ozon по отправлениям.
 */
data class OzonPostingReportRowDto(
    val orderNumber: String = "",
    val postingNumber: String = "",
    val orderFrom: String = "",
    val acceptedAt: LocalDateTime = LocalDateTime.now(),
    val shipmentDate: String = "",
    val status: String = "",
    val deliveryDate: String = "",
    val handedToDeliveryAt: String = "",
    val postingAmount: Double? = null,
    val postingCurrencyCode: String = "",
    val productName: String = "",
    val sku: String = "",
    val offerId: String = "",
    val yourPrice: Double? = null,
    val productCurrencyCode: String = "",
    val buyerPaidAmount: Double? = null,
    val buyerCurrencyCode: String = "",
    val quantity: Int? = null,
    val deliveryCost: Double? = null,
    val relatedPostings: String = "",
    val itemPurchased: String = "",
    val priceBeforeDiscount: Double? = null,
    val discountPercent: String = "",
    val discountRubles: Double? = null,
    val promotions: String = "",
    val volumetricWeightKg: Double? = null,
    val shippingCluster: String = "",
    val deliveryCluster: String = "",
    val shippingWarehouse: String = "",
    val deliveryRegion: String = "",
    val deliveryCity: String = "",
    val deliveryMethod: String = "",
    val customerSegment: String = "",
    val legalEntity: String = "",
    val paymentMethod: String = "",
    val customerAddress: String = "",
    val customerName: String = "",
)

