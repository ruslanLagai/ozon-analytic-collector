package ru.home.ozon.analytic.collector.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "ozon")
data class OzonProperties(
    val url: String,
    val clientId: String,
    val clientSecret: String,
    val perfApiUrl: String
)

