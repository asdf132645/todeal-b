package com.todeal.domain.deal.dto

data class DealResponse(
    val id: Long,
    val title: String,
    val type: String,
    val currentPrice: Int
)
