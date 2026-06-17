package ru.home.ozon.analytic.collector.dto.response.stocks

data class GetStocksAnalyticsResponseDto(
    val items: List<GetStocksAnalyticsItemDto> = emptyList(),
)


