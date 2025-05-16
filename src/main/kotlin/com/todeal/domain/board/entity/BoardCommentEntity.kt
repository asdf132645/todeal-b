// ✅ BoardCommentEntity.kt
package com.todeal.domain.board.entity

import jakarta.persistence.*
import java.time.LocalDateTime

// ✅ BoardCommentEntity.kt
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

    val createdAt: LocalDateTime = LocalDateTime.now()
)
