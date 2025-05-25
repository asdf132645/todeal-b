package com.todeal.domain.hashtag.entity

import jakarta.persistence.*

@Entity
@Table(name = "deal_hashtags")
data class DealHashtagEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val dealId: Long,

    @Column(nullable = false)
    val hashtagId: Long
)
