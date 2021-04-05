package com.kotlin.webmonitoring.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "xup.downtime.user")
data class XupConfig(var notification: String = "")