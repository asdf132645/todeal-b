// UserSignupRequest.kt
package com.todeal.domain.user.dto

data class UserSignupRequest(
    val email: String?,
    val password: String?,
    val nickname: String,
    val phone: String?,
    val profileImageUrl: String? = null,
    val locationAgree: Boolean,
    val latitude: Double?,
    val longitude: Double?,
    val kakaoId: Long? = null,  // 일반 or 카카오 모두 가능
    val agreements: List<String> // ["terms", "privacy", "location"]
)
