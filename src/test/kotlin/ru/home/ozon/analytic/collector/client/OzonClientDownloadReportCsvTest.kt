package ru.home.ozon.analytic.collector.client

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.web.reactive.function.client.WebClient
import java.net.InetSocketAddress

class OzonClientDownloadReportCsvTest {

    private var server: HttpServer? = null

    @AfterEach
    fun tearDown() {
        server?.stop(0)
    }

    @Test
    fun `downloads report csv from absolute url`() {
        val csvBody = """
            \uFEFF\"Номер заказа\";\"Номер отправления\"
            \"1\";\"1-1\"
        """.trimIndent()
            .replace("\\uFEFF", "\uFEFF")

        server = HttpServer.create(InetSocketAddress(0), 0).apply {
            createContext("/report.csv") { exchange: HttpExchange ->
                val responseBytes = csvBody.toByteArray()
                exchange.responseHeaders.add("Content-Type", "text/csv; charset=UTF-8")
                exchange.sendResponseHeaders(200, responseBytes.size.toLong())
                exchange.responseBody.use { it.write(responseBytes) }
            }
            start()
        }

        val port = requireNotNull(server).address.port
        val client = OzonClient(
            ozonWebClient = WebClient.builder().build(),
            reportClient = WebClient.builder().build(),
        )

        val actual = client.downloadReportCsv("http://localhost:$port/report.csv?param=12")

        assertEquals(csvBody, actual)
    }
}

