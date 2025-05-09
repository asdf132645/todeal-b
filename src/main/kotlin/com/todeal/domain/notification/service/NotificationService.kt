package com.todeal.domain.notification.service

import com.todeal.domain.notification.dto.NotificationDto
import com.todeal.domain.notification.dto.NotificationResponse
import com.todeal.domain.notification.entity.NotificationEntity
import com.todeal.domain.notification.repository.NotificationRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class NotificationService(
    private val notificationRepository: NotificationRepository
) {

    @Transactional
    fun createNotification(dto: NotificationDto): NotificationResponse {
        val entity = NotificationEntity(
            userId = dto.userId,
            title = dto.title,
            body = dto.body
        )
        return NotificationResponse.fromEntity(notificationRepository.save(entity))
    }

    @Transactional(readOnly = true)
    fun getUserNotifications(userId: Long): List<NotificationResponse> {
        return notificationRepository.findAllByUserIdOrderByCreatedAtDesc(userId)
            .map { NotificationResponse.fromEntity(it) }
    }

    @Transactional
    fun markAsRead(id: Long) {
        val noti = notificationRepository.findById(id).orElseThrow { RuntimeException("알림이 존재하지 않습니다") }
        noti.isRead = true
        notificationRepository.save(noti)
    }
}
