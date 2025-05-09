// ✅ BidEntity.kt
package com.todeal.domain.bid.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "bids")
data class BidEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val dealId: Long,
    val userId: Long,
    val amount: Int,

    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now()
)
