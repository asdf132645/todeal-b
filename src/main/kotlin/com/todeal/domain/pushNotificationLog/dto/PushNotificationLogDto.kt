// PushNotificationLogDto.kt
package com.todeal.domain.pushNotificationLog.dto

import com.todeal.domain.pushNotificationLog.entity.PushNotificationLogEntity
import java.time.LocalDateTime

data class PushNotificationLogDto(
    val id: Long,
    val userId: Long,
    val title: String,
    val body: String,
    val isRead: Boolean,
    val createdAt: LocalDateTime
) {
    companion object {
        fun from(entity: PushNotificationLogEntity): PushNotificationLogDto {
            return PushNotificationLogDto(
                id = entity.id,
                userId = entity.userId,
                title = entity.title,
                body = entity.body,
                isRead = entity.isRead,
                createdAt = entity.createdAt
            )
        }
    }
}
