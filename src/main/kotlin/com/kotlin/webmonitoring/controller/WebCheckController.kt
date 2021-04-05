package com.kotlin.webmonitoring.controller

import com.kotlin.webmonitoring.domain.WebCheck
import com.kotlin.webmonitoring.domain.WebCheckRequest
import com.kotlin.webmonitoring.service.WebCheckServices
import com.kotlin.webmonitoring.service.WebMonitoringService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.concurrent.TimeUnit

@RestController
@RequestMapping(value = arrayOf("/api"))
class WebCheckController(val webCheckServices: WebCheckServices, val webMonitoringService: WebMonitoringService) {

    @GetMapping("/checks")
    fun allChecks() = webCheckServices.findAll()

    @GetMapping("/checks/{id}")
    fun oneCheck(@PathVariable(value = "id") checkId: Long): ResponseEntity<WebCheck> {
        return webCheckServices.findOne(checkId).map { ResponseEntity.ok(it) }.orElse(ResponseEntity.notFound().build())
    }

    @GetMapping(value = arrayOf("/checks"), params = arrayOf("name"))
    fun checkByName(@RequestParam(value = "name") name: String) = webCheckServices.findAllByName(name)

    @GetMapping(value = arrayOf("/checks"), params = arrayOf("frequency", "frequencyType"))
    fun checkByFrequency(@RequestParam(value = "frequency") frequency: Int, @RequestParam(value = "frequencyType") frequencyType: TimeUnit) = webCheckServices.findAllByFrequency(frequency, frequencyType)

    @GetMapping(value = arrayOf("/checks"), params = arrayOf("url"))
    fun upTime(@RequestParam(value = "url") url: String) = webMonitoringService.findAllByUrl(url)

    @PostMapping("/checks")
    fun createCheck(@RequestBody webCheckRequest: WebCheckRequest): WebCheck {
        val (name, url, frequency, frequencyType) = webCheckRequest
        return webCheckServices.create(name, url, frequency, frequencyType)
    }

    @PutMapping("/checks/{id}")
    fun activate(@PathVariable(value = "id") checkId: Long, @RequestParam(value = "activate") activate: Boolean): ResponseEntity<WebCheck> {
        val newCheck = webCheckServices.activate(checkId, activate)
        return newCheck.map { ResponseEntity.ok(it) }.orElse(ResponseEntity.notFound().build())
    }

}

