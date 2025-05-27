package com.todeal.domain.log.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "login_log")
data class LoginLogEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "user_id", nullable = false)
    val userId: Long,

    @Column(name = "ip_address")
    val ipAddress: String? = null,

    @Column(name = "device_info")
    val deviceInfo: String? = null,

    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now()
)