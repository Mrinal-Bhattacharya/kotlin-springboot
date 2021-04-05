package com.kotlin.webmonitoring.domain

import java.util.concurrent.TimeUnit

data class WebCheckRequest(
        val name: String,
        val webSiteUrl: String,
        val frequency: Int,
        val frequencyType: TimeUnit
)
