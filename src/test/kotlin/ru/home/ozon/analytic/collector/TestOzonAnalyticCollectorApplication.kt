package ru.home.ozon.analytic.collector

import org.springframework.boot.fromApplication
import org.springframework.boot.with


fun main(args: Array<String>) {
    fromApplication<OzonAnalyticCollectorApplication>().with(TestcontainersConfiguration::class).run(*args)
}
