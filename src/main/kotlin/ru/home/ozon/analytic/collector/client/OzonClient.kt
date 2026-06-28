package ru.home.ozon.analytic.collector.client

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientRequestException
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.web.reactive.function.client.bodyToMono
import org.springframework.web.util.UriBuilder
import org.springframework.web.util.UriComponentsBuilder
import reactor.util.retry.Retry
import ru.home.ozon.analytic.collector.dto.request.analytic.AnalyticRequest
import ru.home.ozon.analytic.collector.dto.request.analytic.ProductsQueriesRequest
import ru.home.ozon.analytic.collector.dto.request.report.CreateReportFilterDto
import ru.home.ozon.analytic.collector.dto.request.report.CreateReportRequest
import ru.home.ozon.analytic.collector.dto.request.report.GetReportInfo
import ru.home.ozon.analytic.collector.dto.request.stocks.GetStocksAnalyticsRequest
import ru.home.ozon.analytic.collector.dto.response.analytic.AnalyticResponse
import ru.home.ozon.analytic.collector.dto.response.analytic.AnalyticResultDto
import ru.home.ozon.analytic.collector.dto.response.analytic.ProductsQueriesResponse
import ru.home.ozon.analytic.collector.dto.response.analytic.ProductsQueryDto
import ru.home.ozon.analytic.collector.dto.response.clusters.ClustersResponse
import ru.home.ozon.analytic.collector.dto.response.report.CreateReportResponse
import ru.home.ozon.analytic.collector.dto.response.report.GetReportInfoRepose
import ru.home.ozon.analytic.collector.dto.response.stocks.GetStocksAnalyticsItemDto
import ru.home.ozon.analytic.collector.dto.response.stocks.GetStocksAnalyticsResponseDto
import ru.home.ozon.analytic.collector.exceptions.OzonException
import java.time.Duration
import java.time.LocalDate
import java.time.ZonedDateTime


@Component
class OzonClient(
    @Qualifier("ozonWebClient") val ozonWebClient: WebClient,
    @Qualifier("reportClient") private val reportClient: WebClient
) {

    /**
     * Данные аналитики
     */
    fun analyticData(from: LocalDate, to: LocalDate, metrics: Set<String>, offset: Int): AnalyticResultDto {
        val request = AnalyticRequest(
            dateFrom = from,
            dateTo = to,
            metrics = metrics,
            dimension = setOf("sku", "day"),
            offset = offset
        )

        return ozonWebClient.post()
            .uri("/v1/analytics/data")
            .body(BodyInserters.fromValue(request))
            .retrieve()
            .bodyToMono<AnalyticResponse>()
            .cache(Duration.ofSeconds(5))
            .map { resp ->
                if (resp.code.isNullOrBlank()) {
                    throw OzonException("Error from Ozon API: ${resp.code} - ${resp.message}")
                }
                resp.result ?: throw OzonException("Empty result from Ozon API")
            }
            .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(1)))
            .block()!!
    }

    /**
     * Получение поисковых запросов товаров
     */
    fun productQueries(from: ZonedDateTime, to: ZonedDateTime, skus: List<String>): List<ProductsQueryDto> {
        var page = 0
        val queries = ArrayList<ProductsQueryDto>()
        while (true) {
            val request = ProductsQueriesRequest(
                dateFrom = from,
                dateTo = to,
                skus = skus,
                page = page
            )
            val response = ozonWebClient.post()
                .uri("/v1/analytics/product-queries/details")
                .body(BodyInserters.fromValue(request))
                .retrieve()
                .bodyToMono<ProductsQueriesResponse>()
                .cache(Duration.ofSeconds(5))
                .map { resp ->
                    if (resp.code.isNullOrBlank()) {
                        throw OzonException("Error from Ozon API: ${resp.code} - ${resp.message}")
                    }
                    resp.queries ?: throw OzonException("Empty result from Ozon API")
                    resp
                }
                .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(1)))
                .block()!!
            val pageQueries = response.queries ?: emptyList()
            if (pageQueries.isEmpty()) {
                break
            }
            queries.addAll(pageQueries)
            page++
        }
        return queries
    }

    fun clusterList(): ClustersResponse {
        return ozonWebClient.post()
            .uri("/v2/cluster/list")
            .retrieve()
            .bodyToMono<ClustersResponse>()
            .cache(Duration.ofHours(2))
            .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(1)))
            .block()!!
    }

    /**
     * Получение остатков по кластерам
     */
    fun getStocks(clusters: Set<String>?, skus: Set<String>): List<GetStocksAnalyticsItemDto> {
        val request = GetStocksAnalyticsRequest(
            skus = skus,
            clusterIds = clusters
        )

        return ozonWebClient.post()
            .uri { uriBuilder: UriBuilder ->
                uriBuilder
                    .path("/v1/analytics/stocks")
                    .build()
            }
            .body(BodyInserters.fromValue(request))
            .retrieve()
            .bodyToMono<GetStocksAnalyticsResponseDto>()
            .map { it.items }
            .cache(
                { Duration.ofHours(1) },
                { Duration.ofSeconds(0) },
                { Duration.ofSeconds(0) }
            )
            .retryWhen(Retry.fixedDelay(5, Duration.ofSeconds(1))
                .filter { throwable -> throwable is WebClientRequestException || throwable is WebClientResponseException }
            )
            .block() ?: emptyList()
    }

    /**
     * Создать отчет
     */
    fun createReport(from: ZonedDateTime, to: ZonedDateTime): String {
        val filter = CreateReportFilterDto(processedAtFrom = from, processedAtTo = to)
        val request = CreateReportRequest(filter = filter)
        return ozonWebClient.post()
            .uri { uriBuilder: UriBuilder ->
                uriBuilder
                    .path("/v1/report/postings/create")
                    .build()
            }
            .body(BodyInserters.fromValue(request))
            .retrieve()
            .bodyToMono<CreateReportResponse>()
            .map { resp ->
                resp.result.code
            }
            .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2)))
            .block()!!
    }

    /**
     * Получить статус отчета
     */
    fun getReportInfo(reportId: String): String {
        val request = GetReportInfo(code = reportId)
        return ozonWebClient.post()
            .uri { uriBuilder: UriBuilder ->
                uriBuilder
                    .path("/v1/report/info")
                    .build()
            }
            .body(BodyInserters.fromValue(request))
            .retrieve()
            .bodyToMono<GetReportInfoRepose>()
            .map { resp ->
                if (resp.result.status.equals("success", ignoreCase = true)) {
                    resp.result.file
                } else {
                    throw OzonException("Report generation status: ${resp.result.status}")
                }
            }
            .retryWhen(Retry
                .fixedDelay(3, Duration.ofSeconds(10))
                .filter { throwable -> throwable is OzonException || throwable is WebClientResponseException }
            )
            .block()!!
    }

    /**
     * Получить csv отчет
     */
    fun downloadReportCsv(fileUrl: String): String = reportClient.get()
        .uri { _ ->
            UriComponentsBuilder.fromUriString(fileUrl.replace("%252F", "%2F"))
                .build(true)
                .toUri()
        }
//        .uri(fileUrl)
        .retrieve()
        .bodyToMono<String>()
        .retryWhen(Retry.fixedDelay(2, Duration.ofSeconds(1)))
        .block()!!

}