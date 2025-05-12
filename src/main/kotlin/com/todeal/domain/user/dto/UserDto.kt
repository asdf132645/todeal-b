package com.todeal.domain.user.dto

import com.todeal.domain.user.entity.UserEntity
import java.time.LocalDateTime

data class UserDto(
    val id: Long,
    val email: String?,
    val kakaoId: Long?,
    val nickname: String,
    val phone: String?,
    val profileImageUrl: String?,
    val role: String,
    val isPremium: Boolean,
    val planExpireAt: LocalDateTime?
)

fun UserEntity.toDto() = UserDto(
    id = id,
    email = email,
    kakaoId = kakaoId,
    nickname = nickname,
    phone = phone,
    profileImageUrl = profileImageUrl,
    role = role,
    isPremium = isPremium,
    planExpireAt = planExpireAt
)
