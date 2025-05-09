package com.todeal.domain.pushNotificationLog.dto

import com.todeal.domain.pushNotificationLog.entity.PushNotificationLogEntity
import java.time.LocalDateTime

data class PushNotificationLogResponse(
    val id: Long,
    val title: String,
    val body: String,
    val fcmToken: String,
    val isSuccess: Boolean,
    val responseMessage: String?,
    val createdAt: LocalDateTime
) {
    companion object {
        fun fromEntity(entity: PushNotificationLogEntity): PushNotificationLogResponse {
            return PushNotificationLogResponse(
                id = entity.id,
                title = entity.title,
                body = entity.body,
                fcmToken = entity.fcmToken,
                isSuccess = entity.isSuccess,
                responseMessage = entity.responseMessage,
                createdAt = entity.createdAt
            )
        }
    }
}
