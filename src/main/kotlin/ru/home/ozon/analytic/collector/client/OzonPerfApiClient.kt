package ru.home.ozon.analytic.collector.client

import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import org.springframework.web.util.UriBuilder
import reactor.util.retry.Retry
import ru.home.ozon.analytic.collector.dto.response.perf.GetCampaignDto
import ru.home.ozon.analytic.collector.dto.response.perf.GetCampaignsResponse
import ru.home.ozon.analytic.collector.dto.response.perf.ProductInCampaignDto
import ru.home.ozon.analytic.collector.dto.response.perf.ProductInPromotionInSearchDto
import ru.home.ozon.analytic.collector.dto.response.perf.ProductsInCampaignResponse
import ru.home.ozon.analytic.collector.dto.response.perf.ProductsInPromotionInSearchResponse
import java.time.Duration

@Component
class OzonPerfApiClient(
    val ozonPerfApiWebClient: WebClient
) {

    /**
     * Список камраний (Оплата за клик)
     */
    fun getCampaigns(): List<GetCampaignDto> {
        return ozonPerfApiWebClient.get()
            .uri { uriBuilder: UriBuilder ->
                uriBuilder.path("/campaign")
                    .queryParam("advObjectType", "SKU")
                    .queryParam("state", "CAMPAIGN_STATE_RUNNING")
                    .build()
            }
            .retrieve()
            .bodyToMono<GetCampaignsResponse>()
            .cache(Duration.ofMinutes(5))
            .map { resp -> resp.list }
            .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(1)))
            .block()!!
    }

    /**
     * Список товаров в опалте за клик
     */
    fun getProductsInCampaign(campaign: String): List<ProductInCampaignDto> {
        return ozonPerfApiWebClient.get()
            .uri { uriBuilder: UriBuilder ->
                uriBuilder.path("/campaign")
                    .pathSegment(campaign, "v2/products")
                    .build()
            }
            .retrieve()
            .bodyToMono<ProductsInCampaignResponse>()
            .cache(Duration.ofMinutes(5))
            .map { resp -> resp.products }
            .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(1)))
            .block()!!
    }

    /**
     * Список товаров в ПВП
     */
    fun getProductsInPvp(): List<ProductInPromotionInSearchDto> {
        return ozonPerfApiWebClient.post()
            .uri { uriBuilder: UriBuilder ->
                uriBuilder.path("/campaign/search_promo/v2/products")
                    .build()
            }
            .retrieve()
            .bodyToMono<ProductsInPromotionInSearchResponse>()
            .cache(Duration.ofMinutes(5))
            .map { resp -> resp.products }
            .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(1)))
            .block()!!
    }
}