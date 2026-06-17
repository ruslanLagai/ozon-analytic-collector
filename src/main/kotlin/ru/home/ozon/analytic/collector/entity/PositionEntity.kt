package ru.home.ozon.analytic.collector.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.Version

@Entity
@Table(name = "position_entity")
class PositionEntity(
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE) var id: Long = 0L,
    var sku: String,
    var artikul: String,
    var archive: Boolean,
    var name: String,
    @Version var version: Long? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PositionEntity

        if (archive != other.archive) return false
        if (sku != other.sku) return false
        if (artikul != other.artikul) return false
        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        var result = archive.hashCode()
        result = 31 * result + sku.hashCode()
        result = 31 * result + artikul.hashCode()
        result = 31 * result + name.hashCode()
        return result
    }
}
