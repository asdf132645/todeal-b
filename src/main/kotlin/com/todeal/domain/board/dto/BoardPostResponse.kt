package com.todeal.domain.board.dto

import com.todeal.domain.board.entity.BoardPostEntity

data class BoardPostResponse(
    val id: Long,
    val title: String,
    val content: String,
    val userId: Long,
    val nickname: String,
    val region: String?,
    val commentCount: Int,
    val viewCount: Int,
    val latitude: Double,
    val longitude: Double,
    val category: String,              // ✅ 추가
    val language: String,
    val translatedTitle: String?,
    val translatedContent: String?,
    val createdAt: String
) {
    companion object {
        fun from(entity: BoardPostEntity): BoardPostResponse = BoardPostResponse(
            id = entity.id,
            title = entity.title,
            content = entity.content,
            userId = entity.userId,
            nickname = entity.nickname,
            region = entity.region,
            commentCount = entity.commentCount,
            viewCount = entity.viewCount,
            latitude = entity.latitude,
            longitude = entity.longitude,
            category = entity.category,                // ✅ 매핑 추가
            language = entity.language,
            translatedTitle = entity.translatedTitle,
            translatedContent = entity.translatedContent,
            createdAt = entity.createdAt.toString()
        )
    }
}
