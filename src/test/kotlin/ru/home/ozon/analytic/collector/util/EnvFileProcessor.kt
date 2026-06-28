package ru.home.ozon.analytic.collector.util

import org.springframework.core.env.PropertiesPropertySource
import org.springframework.core.env.PropertySource
import org.springframework.core.io.support.EncodedResource
import org.springframework.core.io.support.PropertySourceFactory
import java.util.Properties

/**
 * @author rlagay
 */
class EnvFileProcessor : PropertySourceFactory {

    override fun createPropertySource(name: String?, encodedResource: EncodedResource): PropertySource<*> {
        val resource = encodedResource.resource.getContentAsString(Charsets.UTF_8)
        val properties = Properties()

        resource.split("\n")
            .map { it.trim() }
            .filter { it.isNotEmpty() && !it.startsWith("#") }
            .forEach {
                val (key, value) = it.split("=", limit = 2)
                properties[key] = value
            }

        return PropertiesPropertySource(encodedResource.resource.filename!!, properties)
    }
}