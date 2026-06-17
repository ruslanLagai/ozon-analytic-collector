package ru.home.ozon.analytic.collector.dto.response.clusters

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test

class ClustersResponseTest {

    private val objectMapper = jacksonObjectMapper()

    @Test
    fun `deserializes clusters response from json resource`() {
        val json = requireNotNull(javaClass.getResource("/analytic/clusters.json"))
            .readText()

        val response = objectMapper.readValue<ClustersResponse>(json)

        assertFalse(response.result.isEmpty())
        assertEquals(4001, response.result.first().macrolocalClusterId)
        assertEquals("Беларусь", response.result.first().data.macrolocalCluster.name)
        assertEquals("23402539267000", response.result.first().data.fulfillments.first().warehouseId)
        assertEquals("1020000890160000", response.result[1].data.fulfillments.first().warehouseId)
    }
}

