package com.kotlin.webmonitoring.controller

import com.ninjasquad.springmockk.MockkBean
import com.kotlin.webmonitoring.domain.WebCheck
import com.kotlin.webmonitoring.domain.WebMonitoring
import com.kotlin.webmonitoring.repository.WebCheckRepository
import com.kotlin.webmonitoring.repository.WebMonitoringRepository
import com.kotlin.webmonitoring.service.WebSchedulerService
import io.mockk.every
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class WebCheckControllerTest(@Autowired val mockMvc: MockMvc) {

    @Autowired
    private lateinit var webCheckRepository: WebCheckRepository

    @Autowired
    private lateinit var webMonitoringRepository: WebMonitoringRepository

    @MockkBean
    private lateinit var webSchedulerService: WebSchedulerService

    @AfterEach
    fun tearDown(){
        this.webCheckRepository.deleteAll()
        this.webMonitoringRepository.deleteAll()
    }

    @Test
    fun `Get all the checks`() {
        val webCheck = webCheckRepository.save(WebCheck(-1, "google", "www.google.com", 10, TimeUnit.DAYS, true))
        val mvcResult = mockMvc.perform(get("/api/checks"))
                .andExpect(status().isOk).andReturn()
        assertThat(mvcResult.response.contentAsString).isEqualTo("""[{"id":${webCheck.id},"name":"google","url":"www.google.com","frequency":10,"frequencyType":"DAYS","active":true}]""")
    }

    @Test
    fun `Get single check based on id`() {
        val webCheck = webCheckRepository.save(WebCheck(-1, "google", "www.google.com", 10, TimeUnit.DAYS, true))
        webCheckRepository.save(WebCheck(-1, "google", "www.google.com", 10, TimeUnit.DAYS, true))
        val mvcResult = mockMvc.perform(get("/api/checks/${webCheck.id}"))
                .andExpect(status().isOk).andReturn()
        assertThat(mvcResult.response.contentAsString).isEqualTo("""{"id":${webCheck.id},"name":"google","url":"www.google.com","frequency":10,"frequencyType":"DAYS","active":true}""")
    }

    @Test
    fun `No result when check not found`() {
        mockMvc.perform(get("/api/checks/2"))
                .andExpect(status().isNotFound).andReturn()
    }

    @Test
    fun `Find check based on name`() {
        val webCheck = webCheckRepository.save(WebCheck(-1, "google", "www.google.com", 10, TimeUnit.DAYS, true))
        val mvcResult = mockMvc.perform(get("/api/checks?name=google"))
                .andExpect(status().isOk).andReturn()
        assertThat(mvcResult.response.contentAsString).isEqualTo("""[{"id":${webCheck.id},"name":"google","url":"www.google.com","frequency":10,"frequencyType":"DAYS","active":true}]""")
    }

    @Test
    fun `Find check based on frequency`() {
        val webCheck = webCheckRepository.save(WebCheck(-1, "google", "www.google.com", 10, TimeUnit.DAYS, true))
        val mvcResult = mockMvc.perform(get("/api/checks?frequency=10&frequencyType=DAYS"))
                .andExpect(status().isOk).andReturn()
        assertThat(mvcResult.response.contentAsString).isEqualTo("""[{"id":${webCheck.id},"name":"google","url":"www.google.com","frequency":10,"frequencyType":"DAYS","active":true}]""")
    }

    @Test
    fun `Find check detail based on url if website is up`() {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val webMonitoring = webMonitoringRepository.save(WebMonitoring(-1, "www.google.com", true, 10))
        val mvcResult = mockMvc.perform(get("/api/checks?url=www.google.com"))
                .andExpect(status().isOk).andReturn()
        assertThat(mvcResult.response.contentAsString).isEqualTo("""{"webSiteUrl":"www.google.com","status":"UP","time":"${webMonitoring.createdDate.format(formatter)}","average":10.0}""")
    }

    @Test
    fun `Find check detail based on url if website is down`() {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val webMonitoring=webMonitoringRepository.save(WebMonitoring(-1, "www.google.com", false, 10))
        webMonitoringRepository.save(WebMonitoring(-1, "www.google.com", false, 10))
        val mvcResult = mockMvc.perform(get("/api/checks?url=www.google.com"))
                .andExpect(status().isOk).andReturn()
        assertThat(mvcResult.response.contentAsString).isEqualTo("""{"webSiteUrl":"www.google.com","status":"DOWN","time":"${webMonitoring.createdDate.format(formatter)}","average":10.0}""")
    }

    @Test
    fun `Deactivate a check`(){
        every { webSchedulerService.unRegisterWebCheck(any()) } returns true
        val webCheck = webCheckRepository.save(WebCheck(-1, "google", "www.google.com", 10, TimeUnit.DAYS, true))
        val mvcResult = mockMvc.perform(put("/api/checks/${webCheck.id}?activate=false"))
                .andExpect(status().isOk).andReturn()
        verify{webSchedulerService.unRegisterWebCheck(any())}
        assertThat(mvcResult.response.contentAsString).isEqualTo("""{"id":${webCheck.id},"name":"google","url":"www.google.com","frequency":10,"frequencyType":"DAYS","active":false}""")
    }

    @Test
    fun `Activate a check`(){
        every { webSchedulerService.registerWebCheck(any()) } answers {}
        val webCheck = webCheckRepository.save(WebCheck(-1, "google", "www.google.com", 10, TimeUnit.DAYS, false))
        val mvcResult = mockMvc.perform(put("/api/checks/${webCheck.id}?activate=true"))
                .andExpect(status().isOk).andReturn()
        verify{webSchedulerService.registerWebCheck(any())}
        assertThat(mvcResult.response.contentAsString).isEqualTo("""{"id":${webCheck.id},"name":"google","url":"www.google.com","frequency":10,"frequencyType":"DAYS","active":true}""")
    }

    @Test
    fun `Create a new check`(){
        every { webSchedulerService.registerWebCheck(any()) } answers {}
        val mvcResult = mockMvc.perform(post("/api/checks").contentType(MediaType.APPLICATION_JSON).content("""{"name" : "Facebook-Check","webSiteUrl" : "www.facebook.com","frequency" : "10","frequencyType" : "SECONDS"}""").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk).andReturn()
        verify{webSchedulerService.registerWebCheck(any())}
        assertThat(mvcResult.response.contentAsString).contains("www.facebook.com")
    }
}