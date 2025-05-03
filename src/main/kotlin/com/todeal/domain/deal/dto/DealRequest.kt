package com.todeal.domain.deal.dto

import java.time.LocalDateTime

data class DealRequest(
    val title: String,
    val description: String,
    val type: String,
    val startPrice: Int,
    val deadline: LocalDateTime,
    val images: List<String>
)
