package com.todeal.domain.bid.dto

data class BidRequest(
    val dealId: Long,
    val userId: Long,
    val amount: Int
)
