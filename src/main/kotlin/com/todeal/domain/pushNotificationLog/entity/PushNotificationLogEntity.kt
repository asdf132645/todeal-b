// PushNotificationLogEntity.kt
package com.todeal.domain.pushNotificationLog.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "push_notification_logs")
data class PushNotificationLogEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val userId: Long,
    val title: String,
    val body: String,
    val isRead: Boolean = false,
    val createdAt: LocalDateTime = LocalDateTime.now()
)

