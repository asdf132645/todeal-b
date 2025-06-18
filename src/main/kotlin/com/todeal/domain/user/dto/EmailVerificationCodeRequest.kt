package com.todeal.domain.user.dto

data class EmailVerificationCodeRequest(
    val email: String,
    val code: String
)
