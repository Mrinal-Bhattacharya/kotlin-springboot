package com.kotlin.webmonitoring.domain

import java.time.LocalDateTime
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class WebMonitoring(
        @Id @GeneratedValue(strategy = GenerationType.AUTO)
        val id: Long,
        val url: String,
        val isUp: Boolean,
        val responseTime: Long,
        val createdDate: LocalDateTime = LocalDateTime.now()) {
    override fun toString(): String {
        return "WebMonitoring Id ${id} Url ${url} IsUp ${isUp} ResponseTime ${responseTime} CreatedDate ${createdDate}"
    }
}
