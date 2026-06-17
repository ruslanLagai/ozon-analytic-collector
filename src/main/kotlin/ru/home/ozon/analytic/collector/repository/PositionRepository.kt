package ru.home.ozon.analytic.collector.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.home.ozon.analytic.collector.entity.PositionEntity

@Repository
interface PositionRepository : JpaRepository<PositionEntity, Long> {
    fun findBySku(sku: String): List<PositionEntity>
    fun findByArtikul(artikul: String): List<PositionEntity>
    fun findByArchiveIsFalse(): List<PositionEntity>
}

