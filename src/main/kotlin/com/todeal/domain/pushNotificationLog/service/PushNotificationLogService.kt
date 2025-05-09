package com.todeal.domain.pushNotificationLog.service

import com.todeal.domain.pushNotificationLog.dto.PushNotificationLogDto
import com.todeal.domain.pushNotificationLog.entity.PushNotificationLogEntity
import com.todeal.domain.pushNotificationLog.repository.PushNotificationLogRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PushNotificationLogService(
    private val repository: PushNotificationLogRepository
) {

    @Transactional
    fun save(dto: PushNotificationLogDto) {
        val entity = PushNotificationLogEntity(
            userId = dto.userId,
            title = dto.title,
            body = dto.body,
            fcmToken = dto.fcmToken,
            isSuccess = dto.isSuccess,
            responseMessage = dto.responseMessage
        )
        repository.save(entity)
    }
}
