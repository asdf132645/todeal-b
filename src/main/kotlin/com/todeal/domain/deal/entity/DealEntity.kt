package com.todeal.domain.deal.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "deals")
data class DealEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val title: String,
    val description: String,
    val type: String, // "used" or "parttime"
    val startPrice: Int,
    val currentPrice: Int,
    val deadline: LocalDateTime,

    @ElementCollection
    @CollectionTable(name = "deal_images", joinColumns = [JoinColumn(name = "deal_id")])
    @Column(name = "image_url")
    val images: List<String> = emptyList()
)

// images는 별도 테이블로 관리되도록 @ElementCollection 사용
