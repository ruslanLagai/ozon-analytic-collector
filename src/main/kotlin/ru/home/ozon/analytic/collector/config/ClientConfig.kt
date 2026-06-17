package ru.home.ozon.analytic.collector.config

import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DefaultDataBufferFactory
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.client.reactive.ClientHttpRequest
import org.springframework.http.client.reactive.ClientHttpRequestDecorator
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import org.springframework.web.reactive.function.client.ClientRequest
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient
import org.reactivestreams.Publisher
import org.springframework.http.HttpMethod
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository
import org.springframework.web.util.DefaultUriBuilderFactory
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import ru.home.ozon.analytic.collector.config.properties.OzonProperties
import java.nio.charset.StandardCharsets

@Configuration
@EnableConfigurationProperties(OzonProperties::class)
class ClientConfig {

	private val log = LoggerFactory.getLogger(ClientConfig::class.java)

	@Bean
	fun ozonWebClient(
        properties: OzonProperties,
	): WebClient = WebClient.builder()
        .defaultHeader("Client-Id", properties.clientId)
        .defaultHeader("Api-Key", properties.clientSecret)
		.baseUrl(properties.url)
		.filter(logRequest())
		.filter(logResponse())
		.build()

	@Bean
	fun ozonPerfApiWebClient(
		clientRegistrations: ReactiveClientRegistrationRepository,
		authorizedClients: ServerOAuth2AuthorizedClientRepository,
		properties: OzonProperties
	): WebClient  {
		val filterOauth2 = ServerOAuth2AuthorizedClientExchangeFilterFunction(
			clientRegistrations,
			authorizedClients
		)
		filterOauth2.setDefaultClientRegistrationId("ozon-perf-api")

		 return WebClient.builder()
			 .baseUrl(properties.perfApiUrl)
			.filter(filterOauth2)
			.filter(logRequest())
			.filter(logResponse())
			.build()
	}

	@Bean
	fun reportClient(): WebClient {
		val factory = DefaultUriBuilderFactory()
		factory.encodingMode = DefaultUriBuilderFactory.EncodingMode.NONE

		return WebClient.builder()
			.uriBuilderFactory(factory)
			.filter(logRequest())
			.filter(logResponse())
			.build()
	}

	private fun logRequest(): ExchangeFilterFunction = ExchangeFilterFunction { request, next ->
		if (request.method() == HttpMethod.GET) {
			if (log.isInfoEnabled) {
				log.info(
					"Ozon request: method={}, url={}, headers={}",
					request.method(),
					request.url(),
					sanitizeHeaders(request.headers())
				)
			}
			next.exchange(request)
		} else {
			val loggedRequest = ClientRequest.from(request)
				.body { outputMessage, context ->
					request.body().insert(
						LoggingClientHttpRequest(outputMessage) { bytes ->
							if (log.isInfoEnabled) {
								log.info(
									"Ozon request: method={}, url={}, headers={}, body={}",
									request.method(),
									request.url(),
									sanitizeHeaders(request.headers()),
									formatBody(bytes),
								)
							}
						},
						context,
					)
				}
				.build()
			next.exchange(loggedRequest)
		}


	}

	private fun logResponse(): ExchangeFilterFunction = ExchangeFilterFunction.ofResponseProcessor { response ->
		if (!shouldLogBody(response.headers().contentType().orElse(null))) {
			if (log.isInfoEnabled) {
				log.info(
					"Ozon response: status={}, headers={}, body=<skipped>",
					response.statusCode(),
					sanitizeHeaders(response.headers().asHttpHeaders()),
				)
			}
			return@ofResponseProcessor Mono.just(response)
		}

		response.bodyToMono(ByteArray::class.java)
			.defaultIfEmpty(ByteArray(0))
			.map { bytes ->
				if (log.isInfoEnabled) {
					log.info(
						"Ozon response: status={}, headers={}, body={}",
						response.statusCode(),
						sanitizeHeaders(response.headers().asHttpHeaders()),
						formatBody(bytes),
					)
				}

				ClientResponse
					.create(response.statusCode())
					.headers { it.addAll(response.headers().asHttpHeaders()) }
					.cookies { it.addAll(response.cookies()) }
					.body(Flux.just(DefaultDataBufferFactory.sharedInstance.wrap(bytes)))
					.build()
			}
	}

	private fun shouldLogBody(contentType: MediaType?): Boolean {
		if (contentType == null) {
			return true
		}

		return MediaType.APPLICATION_JSON.includes(contentType) ||
			MediaType.TEXT_PLAIN.includes(contentType) ||
			MediaType.APPLICATION_XML.includes(contentType) ||
			MediaType.TEXT_XML.includes(contentType) ||
			contentType.type.equals("text", ignoreCase = true)
	}

	private fun formatBody(bytes: ByteArray): String {
		if (bytes.isEmpty()) {
			return "<empty>"
		}

		val body = bytes.toString(StandardCharsets.UTF_8)
		return if (body.length <= MAX_LOGGED_BODY_SIZE) {
			body
		} else {
			body.take(MAX_LOGGED_BODY_SIZE) + "...<truncated>"
		}
	}

	private fun sanitizeHeaders(headers: HttpHeaders): Map<String, List<String>> =
		buildMap {
			headers.forEach { name, values ->
				put(
					name,
					if (
						name.equals(HttpHeaders.AUTHORIZATION, ignoreCase = true) ||
						name.equals("Api-Key", ignoreCase = true) ||
						name.equals("Client-Id", ignoreCase = true)
					) {
						List(values.size) { "***" }
					} else {
						values
					}
				)
			}
		}

	private companion object {
		const val MAX_LOGGED_BODY_SIZE = 8_192
	}

	private class LoggingClientHttpRequest(
		delegate: ClientHttpRequest,
		private val bodyLogger: (ByteArray) -> Unit,
	) : ClientHttpRequestDecorator(delegate) {

		override fun writeWith(body: Publisher<out DataBuffer>): Mono<Void> =
			DataBufferUtils.join(Flux.from(body))
				.defaultIfEmpty(bufferFactory().wrap(ByteArray(0)))
				.flatMap { dataBuffer ->
					val bytes = ByteArray(dataBuffer.readableByteCount())
					dataBuffer.read(bytes)
					DataBufferUtils.release(dataBuffer)
					bodyLogger(bytes)
					super.writeWith(Mono.just(bufferFactory().wrap(bytes)))
				}

		override fun writeAndFlushWith(body: Publisher<out Publisher<out DataBuffer>>): Mono<Void> =
			writeWith(Flux.from(body).concatMap { it })
	}

}
