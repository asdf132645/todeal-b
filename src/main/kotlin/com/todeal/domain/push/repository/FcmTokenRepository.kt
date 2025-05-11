package com.todeal.domain.push.repository

import com.todeal.domain.push.entity.FcmTokenEntity
import org.springframework.data.jpa.repository.JpaRepository

interface FcmTokenRepository : JpaRepository<FcmTokenEntity, Long> {
    fun findByUserId(userId: Long): FcmTokenEntity?
}
