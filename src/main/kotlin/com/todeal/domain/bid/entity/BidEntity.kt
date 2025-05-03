package com.todeal.domain.bid.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "bids")
data class BidEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val dealId: Long,

    @Column(nullable = false)
    val userId: Long,

    @Column(nullable = false)
    val amount: Int,

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)
