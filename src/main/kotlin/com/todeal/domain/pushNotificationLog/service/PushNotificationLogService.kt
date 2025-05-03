// PushNotificationLogService.kt
package com.todeal.domain.pushNotificationLog.service

import com.todeal.domain.pushNotificationLog.dto.PushNotificationLogDto
import com.todeal.domain.pushNotificationLog.dto.PushNotificationLogResponse
import com.todeal.domain.pushNotificationLog.repository.PushNotificationLogRepository
import org.springframework.stereotype.Service

@Service
class PushNotificationLogService(
    private val repository: PushNotificationLogRepository
) {
    fun getUserNotifications(userId: Long): List<PushNotificationLogResponse> {
        return repository.findAllByUserId(userId).map { PushNotificationLogResponse.from(it) }
    }
}
