package com.todeal.domain.bid.dto

data class BidRequest(
    val dealId: Long,
    val nickname: String,
    val amount: Int
)
