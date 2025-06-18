package com.todeal.domain.deal.dto

import com.todeal.domain.deal.model.PricingType
import java.time.LocalDateTime

data class DealResponse(
    val id: Long,
    val title: String,
    val description: String,
    val type: String,
    val pricingType: PricingType,
    val startPrice: Int,
    val currentPrice: Int,
    val deadline: LocalDateTime,
    val region: String,
    val regionDepth1: String,
    val regionDepth2: String,
    val regionDepth3: String,
    val latitude: Double,
    val longitude: Double,
    val images: List<String>,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val winnerBidId: Long? = null,
    val translatedTitle: String? = null,
    val translatedContent: String? = null,
    val language: String? = null,

    // ✅ 커서 필드 추가 (millis 단위 timestamp)
    val cursor: Long = createdAt.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
)
