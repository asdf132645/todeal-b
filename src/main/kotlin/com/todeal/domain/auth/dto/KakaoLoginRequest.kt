// ✅ dto/KakaoLoginRequest.kt
package com.todeal.domain.auth.dto

data class KakaoLoginRequest(
    val accessToken: String,
    val device: String? = null
)
