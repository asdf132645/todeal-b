// dto/NotificationResponse.kt
package com.todeal.domain.notification.dto

import com.todeal.domain.notification.entity.NotificationEntity
import java.time.LocalDateTime

data class NotificationResponse(
    val id: Long,
    val title: String,
    val body: String,
    val isRead: Boolean,
    val createdAt: LocalDateTime
) {
    companion object {
        fun from(entity: NotificationEntity) = NotificationResponse(
            id = entity.id,
            title = entity.title,
            body = entity.body,
            isRead = entity.isRead,
            createdAt = entity.createdAt
        )
    }
}