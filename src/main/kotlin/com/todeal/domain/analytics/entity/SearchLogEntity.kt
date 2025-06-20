package com.todeal.domain.analytics.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "search_logs")
data class SearchLogEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val keyword: String,
    val userId: Long? = null,
    val createdAt: LocalDateTime = LocalDateTime.now()
)
