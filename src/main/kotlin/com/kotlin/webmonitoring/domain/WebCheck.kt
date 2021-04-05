package com.kotlin.webmonitoring.domain

import java.util.concurrent.TimeUnit
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
data class WebCheck(@Id @GeneratedValue(strategy = GenerationType.AUTO)
                    val id: Long, val name: String, val url: String, val frequency: Int, val frequencyType: TimeUnit, val active: Boolean = true) {
    override fun toString(): String {
        return "WebCheck id ${id} name ${name} url ${url} frequency ${frequency} frequency type ${frequencyType}"
    }
}