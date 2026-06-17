package ru.home.ozon.analytic.collector.config

import org.springframework.boot.persistence.autoconfigure.EntityScan
import org.springframework.context.annotation.Configuration

@Configuration
@EntityScan("ru.home.ozon.analytic.collector.entity")
class DbConfig {
}