package com.kotlin.webmonitoring

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class WebMonitoringApplication

fun main(args: Array<String>) {
    runApplication<WebMonitoringApplication>(*args)
}
