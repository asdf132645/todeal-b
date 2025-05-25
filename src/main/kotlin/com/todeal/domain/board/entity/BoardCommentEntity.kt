package com.todeal.domain.board.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "board_comments")
data class BoardCommentEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val postId: Long,

    @Column(nullable = false)
    val userId: Long,

    @Column(nullable = false)
    val content: String,

    @Column(nullable = true)
    val nickname: String? = null,

    // ✅ 댓글 언어 (ex: "ko", "en", "vi")
    @Column(nullable = false)
    val language: String = "ko",

    // ✅ 번역된 내용 (필요 시 프론트에서 번역 API 호출하여 저장)
    @Column(columnDefinition = "TEXT")
    val translatedContent: String? = null,

    val createdAt: LocalDateTime = LocalDateTime.now()
)
