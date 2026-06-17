package ru.home.ozon.analytic.collector.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "google")
data class GoogleDriveProperties(
    val authUri: String,
    val clientId: String,
    val clientSecret: String,
    val tokenUri: String,
    val keyFile: String
)

