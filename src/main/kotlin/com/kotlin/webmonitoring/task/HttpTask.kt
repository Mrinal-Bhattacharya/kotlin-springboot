package com.kotlin.webmonitoring.task

import com.kotlin.webmonitoring.domain.WebCheck
import com.kotlin.webmonitoring.service.WebMonitoringService
import kotlin.system.measureTimeMillis

class HttpTask(val webCheck: WebCheck, val webMonitoringService: WebMonitoringService) : Runnable {

    override fun run(): Unit {
        var webSiteUp: Boolean = false
        val responseTime = measureTimeMillis {
            webSiteUp = webMonitoringService.isWebSiteUp(webCheck.url)
        }
        webMonitoringService.save(webCheck, webSiteUp, responseTime)
        webMonitoringService.notify(webCheck)
    }

}