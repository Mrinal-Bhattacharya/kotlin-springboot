package com.kotlin.webmonitoring.repository

import com.kotlin.webmonitoring.domain.WebMonitoring
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface WebMonitoringRepository : JpaRepository<WebMonitoring, Long> {
    fun findAllByUrl(url: String, pageable: Pageable): List<WebMonitoring>
    fun findAllByUrlOrderByCreatedDate(url: String): List<WebMonitoring>
}
