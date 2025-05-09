package com.todeal.domain.barterBid.dto

data class BarterBidDto(
    val id: Long,
    val dealId: Long,
    val userId: Long,
    val proposedItem: String,
    val description: String,
    val images: List<String>,
    val createdAt: String,
    val isAccepted: Boolean
)
