package com.todeal.domain.user.dto

import com.todeal.domain.user.entity.UserEntity

data class LoginUserDto(
    val id: Long,
    val email: String
) {
    companion object {
        fun from(entity: UserEntity): LoginUserDto {
            return LoginUserDto(
                id = entity.id,
                email = entity.email!!
            )
        }
    }
}
