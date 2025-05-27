package com.todeal.domain.auth.repository

import com.todeal.domain.auth.entity.RefreshTokenEntity
import org.springframework.data.jpa.repository.JpaRepository

interface RefreshTokenRepository : JpaRepository<RefreshTokenEntity, Long> {
    fun findByToken(token: String): RefreshTokenEntity?
    fun deleteByUserId(userId: Long)
    fun findByUserId(userId: Long): RefreshTokenEntity?
}
