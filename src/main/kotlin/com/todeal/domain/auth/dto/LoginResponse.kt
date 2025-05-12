package com.todeal.domain.auth.dto

import com.todeal.domain.user.dto.UserResponse

data class LoginResponse(
    val accessToken: String,
    val refreshToken: String,
    val user: UserResponse
)