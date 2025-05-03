// dto/NotificationDto.kt
package com.todeal.domain.notification.dto

import com.todeal.domain.notification.entity.NotificationEntity
import java.time.LocalDateTime

data class NotificationDto(
    val id: Long,
    val userId: Long,
    val title: String,
    val body: String,
    val isRead: Boolean,
    val createdAt: LocalDateTime
) {
    companion object {
        fun from(entity: NotificationEntity) = NotificationDto(
            id = entity.id,
            userId = entity.userId,
            title = entity.title,
            body = entity.body,
            isRead = entity.isRead,
            createdAt = entity.createdAt
        )
    }
}