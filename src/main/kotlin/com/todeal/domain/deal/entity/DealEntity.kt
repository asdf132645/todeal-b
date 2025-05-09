package com.todeal.domain.deal.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "deals")
data class DealEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val title: String,
    val description: String,
    val type: String, // "used", "parttime", "parttime-request", "barter"

    @Column(name = "user_id", nullable = false)
    val userId: Long,

    val startPrice: Int,
    var currentPrice: Int,
    val deadline: LocalDateTime,

    @Column(nullable = false)
    val region: String, // 예: 서울특별시 강남구 역삼동

    @Column(name = "region_depth1", nullable = false)
    val regionDepth1: String, // 예: 서울특별시

    @Column(name = "region_depth2", nullable = false)
    val regionDepth2: String, // 예: 강남구

    @Column(name = "region_depth3", nullable = false)
    val regionDepth3: String, // 예: 역삼동

    val latitude: Double,
    val longitude: Double,

    @ElementCollection
    @CollectionTable(name = "deal_images", joinColumns = [JoinColumn(name = "deal_id")])
    @Column(name = "image_url")
    val images: List<String> = emptyList(),

    @Column(name = "winner_bid_id")
    var winnerBidId: Long? = null,

    @Column(name = "created_at", updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at")
    var updatedAt: LocalDateTime = LocalDateTime.now()
) {
    @PrePersist
    fun onCreate() {
        createdAt = LocalDateTime.now()
        updatedAt = LocalDateTime.now()
    }

    @PreUpdate
    fun onUpdate() {
        updatedAt = LocalDateTime.now()
    }
}
