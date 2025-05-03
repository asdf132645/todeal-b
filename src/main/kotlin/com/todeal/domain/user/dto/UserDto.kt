package com.todeal.domain.user.dto

import com.todeal.domain.user.entity.UserEntity
import java.time.LocalDateTime

data class UserDto(
    val id: Long,
    val email: String,
    val nickname: String,
    val profileImageUrl: String?,
    val role: String,
    val isPremium: Boolean,
    val planExpireAt: LocalDateTime?
) {
    companion object {
        fun from(entity: UserEntity): UserDto {
            return UserDto(
                id = entity.id,
                email = entity.email,
                nickname = entity.nickname,
                profileImageUrl = entity.profileImageUrl,
                role = entity.role,
                isPremium = entity.isPremium,
                planExpireAt = entity.planExpireAt
            )
        }
    }
}
