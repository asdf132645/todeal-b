package com.todeal.domain.pushNotificationLog.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "push_notification_log")
data class PushNotificationLogEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val userId: Long,

    @Column(nullable = false)
    val title: String,

    @Column(nullable = false)
    val body: String,

    @Column(nullable = false)
    val fcmToken: String,

    @Column(nullable = false)
    val isSuccess: Boolean,

    @Column(nullable = true)
    val responseMessage: String? = null,

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)
