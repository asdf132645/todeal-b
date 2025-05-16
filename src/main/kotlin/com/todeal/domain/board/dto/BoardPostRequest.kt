// ✅ BoardPostRequest.kt
package com.todeal.domain.board.dto

data class BoardPostRequest(
    val title: String,
    val content: String,
    val latitude: Double,
    val longitude: Double,
    val nickname: String,   // ✅ 추가
    val region: String?     // ✅ 추가
)
