package ru.home.ozon.analytic.collector.dto.response.perf

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class GetCampaignsResponse(
    @JsonProperty("list") val list: List<GetCampaignDto> = emptyList(),
    @JsonProperty("total") val total: String = "0",
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class GetCampaignDto(
    @JsonProperty("id") val id: String = "",
    @JsonProperty("title") val title: String = "",
    @JsonProperty("state") val state: String = "",
    @JsonProperty("advObjectType") val advObjectType: String = "",
    @JsonProperty("fromDate") val fromDate: String = "",
    @JsonProperty("toDate") val toDate: String = "",
    @JsonProperty("dailyBudget") val dailyBudget: String = "",
    @JsonProperty("placement") val placement: List<String> = emptyList(),
    @JsonProperty("budget") val budget: String = "",
    @JsonProperty("createdAt") val createdAt: String = "",
    @JsonProperty("updatedAt") val updatedAt: String = "",
    @JsonProperty("productCampaignMode") val productCampaignMode: String = "",
    @JsonProperty("productAutopilotStrategy") val productAutopilotStrategy: String = "",
    @JsonProperty("autopilot") val autopilot: Any? = null,
    @JsonProperty("PaymentType") val paymentType: String = "",
    @JsonProperty("expenseStrategy") val expenseStrategy: String = "",
    @JsonProperty("weeklyBudget") val weeklyBudget: String = "",
    @JsonProperty("budgetType") val budgetType: String = "",
    @JsonProperty("startWeekDay") val startWeekDay: String = "",
    @JsonProperty("endWeekDay") val endWeekDay: String = "",
    @JsonProperty("autoIncrease") val autoIncrease: Any? = null,
    @JsonProperty("ProductAdvPlacements") val productAdvPlacements: List<Any> = emptyList(),
    @JsonProperty("isAutocreated") val isAutocreated: Boolean = false,
    @JsonProperty("autostopStatus") val autostopStatus: String = "",
)

