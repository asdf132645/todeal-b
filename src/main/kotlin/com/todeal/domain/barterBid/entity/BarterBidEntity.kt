package com.todeal.domain.barterBid.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "barter_bid")
data class BarterBidEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val dealId: Long,

    @Column(nullable = false)
    val userId: Long,

    @Column(nullable = false)
    val proposedItem: String,

    @Column(nullable = false, length = 1000)
    val description: String,

    @ElementCollection
    @CollectionTable(
        name = "barter_bid_images",
        joinColumns = [JoinColumn(name = "barter_bid_id")]
    )
    @Column(name = "image_url")
    val images: List<String> = emptyList(),

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: BarterBidStatus = BarterBidStatus.PENDING,

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
) {
    enum class BarterBidStatus {
        PENDING,   // 대기 중
        ACCEPTED,  // 수락됨
        REJECTED   // 거절됨
    }
}
