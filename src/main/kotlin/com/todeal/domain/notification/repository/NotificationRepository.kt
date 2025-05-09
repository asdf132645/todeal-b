package com.todeal.domain.notification.repository

import com.todeal.domain.notification.entity.NotificationEntity
import org.springframework.data.jpa.repository.JpaRepository

interface NotificationRepository : JpaRepository<NotificationEntity, Long> {
    fun findAllByUserIdOrderByCreatedAtDesc(userId: Long): List<NotificationEntity>
}
