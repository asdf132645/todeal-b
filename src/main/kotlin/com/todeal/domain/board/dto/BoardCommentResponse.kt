package com.todeal.domain.board.dto

import com.todeal.domain.board.entity.BoardCommentEntity

data class BoardCommentResponse(
    val id: Long,
    val postId: Long,
    val userId: Long,
    val nickname: String,
    val content: String,
    val createdAt: String
) {
    companion object {
        fun from(entity: BoardCommentEntity): BoardCommentResponse = BoardCommentResponse(
            id = entity.id,
            postId = entity.postId,
            userId = entity.userId,
            nickname = entity.nickname ?: "익명", // ← null safe
            content = entity.content,
            createdAt = entity.createdAt.toString()
        )
    }
}
