package ru.home.ozon.analytic.collector.dto.response.report

data class CreateReportResponse(
    val result: CreateReportResultDto = CreateReportResultDto(),
)

data class CreateReportResultDto(
    val code: String = "",
)
