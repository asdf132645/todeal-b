package com.todeal.domain.auth.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "refresh_token")
data class RefreshTokenEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "user_id", nullable = false)
    val userId: Long,

    @Column(nullable = false, columnDefinition = "TEXT")
    val token: String,

    @Column(name = "expire_at", nullable = false)
    val expireAt: LocalDateTime,

    @Column(name = "device_info")
    val deviceInfo: String? = null,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)
