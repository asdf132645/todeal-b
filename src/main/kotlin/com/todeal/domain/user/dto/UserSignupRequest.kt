// UserSignupRequest.kt
package com.todeal.domain.user.dto

data class UserSignupRequest(
    val email: String,
    val password: String,
    val nickname: String,
    val phone: String?,
    val profileImageUrl: String?,
    val locationAgree: Boolean,
    val latitude: Double?,
    val longitude: Double?,
    val agreements: List<String>  // ["terms", "marketing"] 등
)

