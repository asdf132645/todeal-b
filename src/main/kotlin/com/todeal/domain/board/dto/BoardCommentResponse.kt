package com.todeal.domain.board.dto

import com.todeal.domain.board.entity.BoardCommentEntity

data class BoardCommentResponse(
    val id: Long,
    val postId: Long,
    val userId: Long,
    val nickname: String,
    val content: String,
    val language: String,               // ✅ 추가: 원문 언어
    val translatedContent: String?,     // ✅ 추가: 번역된 내용 (nullable)
    val createdAt: String
) {
    companion object {
        fun from(entity: BoardCommentEntity): BoardCommentResponse = BoardCommentResponse(
            id = entity.id,
            postId = entity.postId,
            userId = entity.userId,
            nickname = entity.nickname ?: "익명",
            content = entity.content,
            language = entity.language,
            translatedContent = entity.translatedContent,
            createdAt = entity.createdAt.toString()
        )
    }
}
