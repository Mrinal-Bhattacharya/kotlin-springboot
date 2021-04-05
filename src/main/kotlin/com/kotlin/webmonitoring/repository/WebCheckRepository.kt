package com.kotlin.webmonitoring.repository

import com.kotlin.webmonitoring.domain.WebCheck
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.concurrent.TimeUnit

@Repository
interface WebCheckRepository : JpaRepository<WebCheck, Long> {
    fun findAllByName(name: String): List<WebCheck>
    fun findAllByFrequencyAndFrequencyType(frequency: Int, frequencyType: TimeUnit): List<WebCheck>

}
