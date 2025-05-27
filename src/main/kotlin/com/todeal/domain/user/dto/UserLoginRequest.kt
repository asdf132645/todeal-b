package com.todeal.domain.user.dto

data class UserLoginRequest(
    val email: String,
    val password: String,
    val ip: String? = null,
    val device: String? = null
)
