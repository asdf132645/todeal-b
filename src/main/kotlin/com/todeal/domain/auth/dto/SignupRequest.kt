package com.todeal.domain.auth.dto

data class SignupRequest(
    val nickname: String,
    val phone: String,
    val email: String?, // ✅ 이거 추가
    val locationAgree: Boolean,
    val latitude: Double?,
    val longitude: Double?,
    val agreements: List<String> // ["terms", "privacy", "location"]
)