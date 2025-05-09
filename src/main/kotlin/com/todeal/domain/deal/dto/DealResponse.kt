package com.todeal.domain.deal.dto

import java.time.LocalDateTime

data class DealResponse(
    val id: Long,
    val title: String,
    val description: String,
    val type: String,
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
    val winnerBidId: Long? = null
)
