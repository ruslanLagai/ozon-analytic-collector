package ru.home.ozon.analytic.collector.service

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class OzonPostingReportCsvParserTest {

    private val parser = OzonPostingReportCsvParser()

    @Test
    fun `parses posting report csv`() {
        val csv = requireNotNull(javaClass.getResource("/analytic/posting-report.csv"))
            .readText()

        val rows = parser.parse(csv)

        Assertions.assertEquals(1, rows.size)

        val row = rows.first()
        Assertions.assertEquals("81342265-0828", row.orderNumber)
        Assertions.assertEquals("81342265-0828-1", row.postingNumber)
        Assertions.assertEquals("Доставляется", row.status)
        Assertions.assertEquals(1980.00, row.postingAmount)
        Assertions.assertEquals("RUB", row.postingCurrencyCode)
        Assertions.assertEquals("1134715033", row.sku)
        Assertions.assertEquals(1, row.quantity)
        Assertions.assertNull(row.deliveryCost)
        Assertions.assertEquals(2500.00, row.priceBeforeDiscount)
        Assertions.assertEquals("21%", row.discountPercent)
        Assertions.assertEquals(520.00, row.discountRubles)
        Assertions.assertEquals("Самара", row.shippingCluster)
        Assertions.assertEquals("ПВЗ", row.deliveryMethod)
        Assertions.assertEquals("Ozon Банк", row.paymentMethod)
        Assertions.assertEquals("", row.customerAddress)
        Assertions.assertEquals("", row.customerName)
    }
}