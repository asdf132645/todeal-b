package com.todeal.domain.board.dto

data class BoardPostRequest(
    val title: String,
    val content: String,
    val category: String,               // ✅ 추가된 부분
    val latitude: Double,
    val longitude: Double,
    val nickname: String,
    val region: String?,

    // ✅ 게시글 언어 (예: "ko", "en", "vi")
    val language: String,

    // ✅ 번역된 제목 (선택)
    val translatedTitle: String? = null,

    // ✅ 번역된 내용 (선택)
    val translatedContent: String? = null
)
