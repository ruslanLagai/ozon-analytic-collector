package ru.home.ozon.analytic.collector

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class OzonAnalyticCollectorApplication

fun main(args: Array<String>) {
    runApplication<OzonAnalyticCollectorApplication>(*args)
}
