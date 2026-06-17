package ru.home.ozon.analytic.collector.service

import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVRecord
import org.springframework.stereotype.Service
import ru.home.ozon.analytic.collector.dto.ozon.report.OzonPostingReportRowDto
import java.io.StringReader

@Service
class OzonPostingReportCsvParser {

    fun parse(csv: String): List<OzonPostingReportRowDto> {
        if (csv.isBlank()) {
            return emptyList()
        }

        val format = CSVFormat.DEFAULT.builder()
            .setDelimiter(';')
            .setQuote('"')
            .setHeader()
            .setSkipHeaderRecord(true)
            .setIgnoreSurroundingSpaces(true)
            .setTrim(true)
            .build()

        CSVParser(StringReader(removeUtf8Bom(csv)), format).use { parser ->
            return parser.records.map(::toRowDto)
        }
    }

    private fun toRowDto(record: CSVRecord): OzonPostingReportRowDto = OzonPostingReportRowDto(
        orderNumber = record.get("Номер заказа"),
        postingNumber = record.get("Номер отправления"),
        acceptedAt = record.get("Принят в обработку"),
        shipmentDate = record.get("Дата отгрузки"),
        status = record.get("Статус"),
        deliveryDate = record.get("Дата доставки"),
        handedToDeliveryAt = record.get("Фактическая дата передачи в доставку"),
        postingAmount = record.doubleValue("Сумма отправления"),
        postingCurrencyCode = record.get("Код валюты отправления"),
        productName = record.get("Название товара"),
        sku = record.get("SKU"),
        offerId = record.get("Артикул"),
        yourPrice = record.doubleValue("Ваша цена"),
        productCurrencyCode = record.get("Код валюты товара"),
        buyerPaidAmount = record.doubleValue("Оплачено покупателем"),
        buyerCurrencyCode = record.get("Код валюты покупателя"),
        quantity = record.intValue("Количество"),
        deliveryCost = record.doubleValue("Стоимость доставки"),
        relatedPostings = record.get("Связанные отправления"),
        itemPurchased = record.get("Выкуп товара"),
        priceBeforeDiscount = record.doubleValue("Цена товара до скидок"),
        discountPercent = record.get("Скидка %"),
        discountRubles = record.doubleValue("Скидка руб"),
        promotions = record.get("Акции"),
        volumetricWeightKg = record.doubleValue("Объемный вес товаров, кг"),
        shippingCluster = record.get("Кластер отгрузки"),
        deliveryCluster = record.get("Кластер доставки"),
        shippingWarehouse = record.get("Склад отгрузки"),
        deliveryRegion = record.get("Регион доставки"),
        deliveryCity = record.get("Город доставки"),
        deliveryMethod = record.get("Способ доставки"),
        customerSegment = record.get("Сегмент клиента"),
        legalEntity = record.get("Юридическое лицо"),
        paymentMethod = record.get("Способ оплаты"),
        customerAddress = record.get("Адрес покупателя"),
        customerName = record.get("Имя покупателя"),
    )

    private fun CSVRecord.doubleValue(header: String): Double? = get(header)
        .takeIf { it.isNotBlank() }
        ?.replace(',', '.')?.toDouble()

    private fun CSVRecord.intValue(header: String): Int? = get(header)
        .takeIf { it.isNotBlank() }
        ?.toInt()

    private fun removeUtf8Bom(csv: String): String = csv.removePrefix("\uFEFF")
}

