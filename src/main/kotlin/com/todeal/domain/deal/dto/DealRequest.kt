package com.todeal.domain.deal.dto

import java.time.LocalDateTime

data class DealRequest(
    val title: String,
    val description: String,
    val type: String, // "used", "parttime", "barter", "parttime-request"
    val startPrice: Int,
    val deadline: LocalDateTime,
    val region: String,
    val regionDepth1: String,
    val regionDepth2: String,
    val regionDepth3: String,
    val latitude: Double,
    val longitude: Double,
    val images: List<String> = emptyList()
)
