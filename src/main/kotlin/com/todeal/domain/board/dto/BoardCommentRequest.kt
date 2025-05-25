package com.todeal.domain.board.dto

data class BoardCommentRequest(
    val postId: Long,
    val content: String,
    val nickname: String,

    // ✅ 댓글 작성 언어 (예: "ko", "en", "vi")
    val language: String,

    // ✅ 번역된 댓글 내용 (nullable)
    val translatedContent: String? = null
)
