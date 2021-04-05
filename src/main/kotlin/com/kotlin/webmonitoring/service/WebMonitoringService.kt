package com.kotlin.webmonitoring.service

import com.kotlin.webmonitoring.config.XupConfig
import com.kotlin.webmonitoring.domain.WebCheck
import com.kotlin.webmonitoring.domain.WebCheckResponse
import com.kotlin.webmonitoring.domain.WebMonitoring
import com.kotlin.webmonitoring.repository.WebMonitoringRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.io.IOException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.time.format.DateTimeFormatter

@Service
class WebMonitoringService(val webMonitoringRepository: WebMonitoringRepository, val xupConfig: XupConfig) {

    fun isWebSiteUp(urlPath: String): Boolean {
        try {
            val url = URL(urlPath)
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
            connection.setRequestProperty("Connection", "close")
            connection.requestMethod = "HEAD"
            connection.connectTimeout = 3000
            connection.connect()
            return when (connection.responseCode) {
                in 200..399 -> true
                else -> false
            }
        } catch (e: Exception) {
            when (e) {
                is MalformedURLException -> println("loadLink: Invalid URL ${e.message}")
                is IOException -> println("loadLink: IO Exception reading data: ${e.message}")
                else -> println("Unknown error: ${e.message}")
            }
        }
        return false
    }

    fun save(webCheck: WebCheck, webSiteUp: Boolean, responseTime: Long): WebMonitoring {
        return webMonitoringRepository.save(WebMonitoring(-1, webCheck.url, webSiteUp, responseTime))
    }

    fun notify(webCheck: WebCheck) {
        val pageable = PageRequest.of(0, xupConfig.notification.toInt(), Sort.Direction.DESC, "createdDate")
        val list = webMonitoringRepository.findAllByUrl(webCheck.url, pageable)
        val count = list.filter { it.isUp != true }.count()
        if (count == xupConfig.notification.toInt()) {
            println("Notification send to ${webCheck}")
        }
    }

    fun findAllByUrl(url: String): WebCheckResponse {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val allByUrl = webMonitoringRepository.findAllByUrlOrderByCreatedDate(url)
        val average = allByUrl.map { it.responseTime }.average()
        val lastStatus = allByUrl.get(allByUrl.size - 1)
        when {
            lastStatus.isUp -> {
                val findLastDown = allByUrl.findLast { !it.isUp }
                        ?: return WebCheckResponse(allByUrl[0].url, "UP", allByUrl[0].createdDate.format(formatter), average)
                val lastUp = allByUrl.get(allByUrl.lastIndexOf(findLastDown))
                return WebCheckResponse(lastUp.url, "UP", lastUp.createdDate.format(formatter), average)
            }
            else -> {
                val findLastUp = allByUrl.findLast { it.isUp }
                        ?: return WebCheckResponse(allByUrl[0].url, "DOWN", allByUrl[0].createdDate.format(formatter), average)
                val lastDown = allByUrl.get(allByUrl.lastIndexOf(findLastUp))
                return WebCheckResponse(lastDown.url, "DOWN", lastDown.createdDate.format(formatter), average)
            }
        }
    }

}
