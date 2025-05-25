package com.todeal.domain.user.dto

import com.todeal.domain.user.entity.UserEntity
import java.time.LocalDateTime

data class UserResponse(
    val id: Long,
    val email: String?, // nullable 허용
    val nickname: String,
    val profileImageUrl: String?,
    val role: String,
    val isPremium: Boolean,
    val planExpireAt: LocalDateTime?,
    val trustScore: Double
) {
    companion object {
        fun from(entity: UserEntity): UserResponse {
            return UserResponse(
                id = entity.id,
                email = entity.email,
                nickname = entity.nickname,
                profileImageUrl = entity.profileImageUrl,
                role = entity.role,
                isPremium = entity.isPremium,
                planExpireAt = entity.planExpireAt,
                trustScore = entity.trustScore
            )
        }
    }
}
