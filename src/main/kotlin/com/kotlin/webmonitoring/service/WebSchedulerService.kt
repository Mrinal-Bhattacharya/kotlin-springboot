package com.kotlin.webmonitoring.service

import com.kotlin.webmonitoring.domain.WebCheck
import com.kotlin.webmonitoring.task.HttpTask
import org.springframework.stereotype.Service
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import javax.annotation.PreDestroy

@Service
class WebSchedulerService(val webMonitoringService: WebMonitoringService) {

    private val scheduledTasks: HashMap<Long, ScheduledFuture<*>> = HashMap<Long, ScheduledFuture<*>>()
    private val scheduleExecutorService: ScheduledExecutorService = Executors.newScheduledThreadPool(50)


    fun registerWebCheck(webCheck: WebCheck) {
        val scheduledFuture = scheduleExecutorService.scheduleAtFixedRate(HttpTask(webCheck, webMonitoringService),
                0,
                webCheck.frequency.toLong(),
                webCheck.frequencyType
        )
        scheduledTasks[webCheck.id] = scheduledFuture
    }

    fun unRegisterWebCheck(webCheck: WebCheck) = scheduledTasks[webCheck.id]?.cancel(false)

    @PreDestroy
    fun shutdown() = scheduleExecutorService.shutdown()

}