package com.todeal.domain.user.dto

import com.todeal.domain.user.entity.UserEntity

data class LoginUserDto(
    val id: Long,
    val email: String,
    val nickname: String,               // ✅ 추가
    val isPremium: Boolean              // (선택) 유료 여부까지 줄 수 있음
) {
    companion object {
        fun from(entity: UserEntity): LoginUserDto {
            return LoginUserDto(
                id = entity.id,
                email = entity.email ?: "",
                nickname = entity.nickname ?: "익명",              // ✅ 닉네임 매핑
                isPremium = entity.isPremium
            )
        }
    }
}
