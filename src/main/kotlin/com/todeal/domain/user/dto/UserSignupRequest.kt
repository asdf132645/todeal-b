// UserSignupRequest.kt
package com.todeal.domain.user.dto

data class UserSignupRequest(
    val email: String,
    val password: String,
    val nickname: String,
    val profileImageUrl: String? = null
)
