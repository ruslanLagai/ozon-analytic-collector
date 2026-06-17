package ru.home.ozon.analytic.collector.dto.response.analytic

data class AnalyticResponse(
    val result: AnalyticResultDto? = AnalyticResultDto(),
    val timestamp: String? = "",
    val code: String? = "",
    val message: String? = "",
)

data class AnalyticResultDto(
    val data: List<AnalyticDataItemDto> = emptyList(),
    val totals: List<Long> = emptyList(),
)

data class AnalyticDataItemDto(
    val dimensions: List<AnalyticDimensionDto> = emptyList(),
    val metrics: List<Long> = emptyList(),
)

data class AnalyticDimensionDto(
    val id: String = "",
    val name: String = "",
)

