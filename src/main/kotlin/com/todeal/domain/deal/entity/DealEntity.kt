package com.todeal.domain.deal.entity

import com.todeal.domain.deal.model.PricingType
import jakarta.persistence.*
import java.time.LocalDateTime

enum class DealStatus {
    ACTIVE, EXPIRED, DELETED
}

@Entity
@Table(name = "deals")
data class DealEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    var title: String,
    var description: String,
    var type: String, // "used", "parttime", "parttime-request", "barter"

    @Enumerated(EnumType.STRING)
    @Column(name = "pricing_type", nullable = false)
    var pricingType: PricingType = PricingType.BIDDING,

    @Column(name = "user_id", nullable = false)
    val userId: Long,

    var startPrice: Int,
    var currentPrice: Int,
    var deadline: LocalDateTime,

    @Column(nullable = false)
    var region: String,

    @Column(name = "region_depth1", nullable = false)
    var regionDepth1: String,

    @Column(name = "region_depth2", nullable = false)
    var regionDepth2: String,

    @Column(name = "region_depth3", nullable = false)
    var regionDepth3: String,

    @Column(name = "is_promoted", nullable = false)
    var isPromoted: Boolean = false,

    @Column(name = "promotion_expire_at")
    var promotionExpireAt: LocalDateTime? = null,

    var latitude: Double,
    var longitude: Double,

    @ElementCollection
    @CollectionTable(name = "deal_images", joinColumns = [JoinColumn(name = "deal_id")])
    @Column(name = "image_url")
    var images: List<String> = emptyList(),

    @Column(name = "winner_bid_id")
    var winnerBidId: Long? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: DealStatus = DealStatus.ACTIVE,

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

    fun update(
        title: String,
        description: String,
        type: String,
        pricingType: PricingType,
        startPrice: Int,
        deadline: LocalDateTime,
        region: String,
        regionDepth1: String,
        regionDepth2: String,
        regionDepth3: String,
        latitude: Double,
        longitude: Double,
        images: List<String>
    ) {
        this.title = title
        this.description = description
        this.type = type
        this.pricingType = pricingType
        this.startPrice = startPrice
        this.deadline = deadline
        this.region = region
        this.regionDepth1 = regionDepth1
        this.regionDepth2 = regionDepth2
        this.regionDepth3 = regionDepth3
        this.latitude = latitude
        this.longitude = longitude
        this.images = images
    }
}
