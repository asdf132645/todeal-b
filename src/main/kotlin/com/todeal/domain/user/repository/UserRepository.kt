package com.todeal.domain.user.repository

import com.todeal.domain.user.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<UserEntity, Long> {
    fun findByEmail(email: String): UserEntity?
    fun findByIdIn(ids: Set<Long>): List<UserEntity>
    fun findByNickname(nickname: String): UserEntity?
    fun findByKakaoId(kakaoId: Long): UserEntity?
    fun existsByEmail(email: String): Boolean
}
