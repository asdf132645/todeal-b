// ✅ BoardPostEntity.kt
package com.todeal.domain.board.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "board_posts")
data class BoardPostEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val userId: Long,

    @Column(nullable = false)
    val title: String,

    @Column(nullable = false, columnDefinition = "TEXT")
    val content: String,

    @Column(nullable = false)
    val latitude: Double,

    @Column(nullable = false)
    val longitude: Double,

    @Column(nullable = false)
    val nickname: String,

    @Column(nullable = true)
    val region: String? = null,

    var commentCount: Int = 0,
    var viewCount: Int = 0,

    val createdAt: LocalDateTime = LocalDateTime.now(),
    var updatedAt: LocalDateTime = LocalDateTime.now()
)
