package com.kotlin.webmonitoring.domain

data class WebCheckResponse(
        val webSiteUrl: String,
        val status: String,
        val time: String,
        val average: Double
)
