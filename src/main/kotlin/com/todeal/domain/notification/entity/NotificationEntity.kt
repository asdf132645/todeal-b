// entity/NotificationEntity.kt
package com.todeal.domain.notification.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "notifications")
data class NotificationEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val userId: Long,
    val title: String,
    val body: String,
    val isRead: Boolean = false,
    val createdAt: LocalDateTime = LocalDateTime.now()
)
