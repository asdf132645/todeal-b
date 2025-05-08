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

    val latitude: Double,     // ← 위치 필드
    val longitude: Double,    // ← 위치 필드

    @ElementCollection
    @CollectionTable(name = "deal_images", joinColumns = [JoinColumn(name = "deal_id")])
    @Column(name = "image_url")
    val images: List<String> = emptyList()
)
fun DealEntity.toResponse(): Map<String, Any> {
    return mapOf(
        "id" to id,
        "title" to title,
        "description" to description,
        "type" to type,
        "startPrice" to startPrice,
        "currentPrice" to currentPrice,
        "deadline" to deadline,
        "images" to images,
        "latitude" to latitude,
        "longitude" to longitude
    )
}

// images는 별도 테이블로 관리되도록 @ElementCollection 사용
