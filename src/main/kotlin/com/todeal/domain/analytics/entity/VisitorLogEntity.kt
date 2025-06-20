package com.todeal.domain.analytics.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "visitor_logs")
data class VisitorLogEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val ip: String,
    val userAgent: String,
    val path: String,

    val userId: Long? = null,
    val createdAt: LocalDateTime = LocalDateTime.now()
)
