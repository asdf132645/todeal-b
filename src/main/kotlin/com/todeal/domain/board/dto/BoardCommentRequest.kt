package com.todeal.domain.board.dto

data class BoardCommentRequest(
    val postId: Long,
    val content: String,
    val nickname: String  // ✅ 추가
)
