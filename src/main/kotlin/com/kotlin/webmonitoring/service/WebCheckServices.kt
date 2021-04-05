package com.kotlin.webmonitoring.service

import com.kotlin.webmonitoring.domain.WebCheck
import com.kotlin.webmonitoring.repository.WebCheckRepository
import org.springframework.stereotype.Service
import java.util.*
import java.util.concurrent.TimeUnit

@Service
class WebCheckServices(val webCheckRepository: WebCheckRepository, val webSchedulerService: WebSchedulerService) {

    fun findAll(): MutableList<WebCheck> = webCheckRepository.findAll()

    fun findOne(id: Long) = webCheckRepository.findById(id)

    fun findAllByName(name: String) = webCheckRepository.findAllByName(name)

    fun findAllByFrequency(frequency: Int, frequencyType: TimeUnit) = webCheckRepository.findAllByFrequencyAndFrequencyType(frequency, frequencyType)

    fun activate(checkId: Long, activate: Boolean): Optional<WebCheck> {
        val webCheck = webCheckRepository.findById(checkId)
        val newWebCheck = webCheck.map {
            webCheckRepository.save(it.copy(active = activate))
        }
        if (activate) {
            newWebCheck.map { webSchedulerService.registerWebCheck(it) }
        } else {
            newWebCheck.map { webSchedulerService.unRegisterWebCheck(it) }
        }
        return newWebCheck
    }

    fun create(name: String, url: String, frequencyInMinutes: Int, frequencyType: TimeUnit): WebCheck {
        val check = webCheckRepository.save(WebCheck(-1, name, url, frequencyInMinutes, frequencyType))
        webSchedulerService.registerWebCheck(check)
        return check
    }
}
