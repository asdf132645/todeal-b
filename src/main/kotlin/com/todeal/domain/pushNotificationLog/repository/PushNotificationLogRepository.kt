package com.todeal.domain.pushNotificationLog.repository

import com.todeal.domain.pushNotificationLog.entity.PushNotificationLogEntity
import org.springframework.data.jpa.repository.JpaRepository

interface PushNotificationLogRepository : JpaRepository<PushNotificationLogEntity, Long> {
    fun findAllByUserIdOrderByCreatedAtDesc(userId: Long): List<PushNotificationLogEntity>
}
