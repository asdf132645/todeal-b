package com.todeal.domain.barterBid.dto

data class BarterBidRequest(
    val dealId: Long,
    val proposedItem: String,
    val description: String,
    val images: List<String> = emptyList()
)
