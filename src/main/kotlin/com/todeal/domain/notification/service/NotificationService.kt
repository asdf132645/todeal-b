// service/NotificationService.kt
package com.todeal.domain.notification.service

import com.todeal.domain.notification.dto.*
import com.todeal.domain.notification.entity.NotificationEntity
import com.todeal.domain.notification.repository.NotificationRepository
import org.springframework.stereotype.Service

@Service
class NotificationService(
    private val notificationRepository: NotificationRepository
) {
    fun create(request: NotificationDto): NotificationDto {
        val entity = NotificationEntity(
            userId = request.userId,
            title = request.title,
            body = request.body
        )
        return NotificationDto.from(notificationRepository.save(entity))
    }

    fun getByUser(userId: Long): List<NotificationDto> {
        return notificationRepository.findAllByUserId(userId).map { NotificationDto.from(it) }
    }

    fun markAsRead(id: Long): NotificationDto {
        val notification = notificationRepository.findById(id).orElseThrow()
        val updated = notification.copy(isRead = true)
        return NotificationDto.from(notificationRepository.save(updated))
    }
}
