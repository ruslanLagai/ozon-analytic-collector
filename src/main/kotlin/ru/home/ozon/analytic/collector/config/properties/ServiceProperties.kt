package ru.home.ozon.analytic.collector.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.NestedConfigurationProperty

@ConfigurationProperties(prefix = "service")
data class ServiceProperties(
    @NestedConfigurationProperty val marketing: Marketing,
    @NestedConfigurationProperty val analytic: Analytic,
    @NestedConfigurationProperty val googleDriveFiles: GoogleDriveFiles

)

data class GoogleDriveFiles(
    val marketing: String,
    val analytic: String,
    val output: String
)

data class Analytic(
    val skuToCategory: Map<String, String>
)

data class Marketing(
    val positions: Map<String, Int>,
    val skuToGroup: Map<String, String>,
)

