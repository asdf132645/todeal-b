package com.todeal.domain.user.dto

data class PasswordResetRequest(
    val email: String
)

data class PasswordResetConfirmRequest(
    val token: String,
    val newPassword: String
)
