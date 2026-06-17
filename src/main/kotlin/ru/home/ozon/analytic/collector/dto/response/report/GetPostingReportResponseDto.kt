package ru.home.ozon.analytic.collector.dto.response.report

data class GetPostingReportResponseDto(
    val result: GetPostingReportResultDto = GetPostingReportResultDto(),
)

data class GetPostingReportResultDto(
    val code: String = "",
)

