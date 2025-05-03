// PushNotificationLogResponse.kt
package com.todeal.domain.pushNotificationLog.dto

import com.todeal.domain.pushNotificationLog.entity.PushNotificationLogEntity
import java.time.LocalDateTime

data class PushNotificationLogResponse(
    val id: Long,
    val title: String,
    val body: String,
    val isRead: Boolean,
    val createdAt: LocalDateTime
) {
    companion object {
        fun from(entity: PushNotificationLogEntity): PushNotificationLogResponse {
            return PushNotificationLogResponse(
                id = entity.id,
                title = entity.title,
                body = entity.body,
                isRead = entity.isRead,
                createdAt = entity.createdAt
            )
        }
    }
}
