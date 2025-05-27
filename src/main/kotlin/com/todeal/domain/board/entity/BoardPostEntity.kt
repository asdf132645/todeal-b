package com.todeal.domain.board.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "board_posts")
data class BoardPostEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    var userId: Long,

    @Column(nullable = false)
    var title: String,

    @Column(nullable = false, columnDefinition = "TEXT")
    var content: String,

    @Column(nullable = false)
    var category: String,  // ✅ 추가됨

    @Column(nullable = false)
    var latitude: Double,

    @Column(nullable = false)
    var longitude: Double,

    @Column(nullable = false)
    var nickname: String,

    @Column(nullable = true)
    var region: String? = null,

    @Column(nullable = false)
    var language: String = "ko",

    @Column(nullable = true)
    var translatedTitle: String? = null,

    @Column(nullable = true, columnDefinition = "TEXT")
    var translatedContent: String? = null,

    @ElementCollection
    @CollectionTable(name = "board_post_images", joinColumns = [JoinColumn(name = "board_post_id")])
    @Column(name = "image_url")
    var imageUrls: List<String> = emptyList(),


    var commentCount: Int = 0,
    var viewCount: Int = 0,

    val createdAt: LocalDateTime = LocalDateTime.now(),
    var updatedAt: LocalDateTime = LocalDateTime.now()
)
