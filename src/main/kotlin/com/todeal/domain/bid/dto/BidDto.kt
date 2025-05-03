package com.todeal.domain.bid.dto

import com.todeal.domain.bid.entity.BidEntity
import java.time.LocalDateTime

data class BidDto(
    val id: Long,
    val dealId: Long,
    val userId: Long,
    val amount: Int,
    val createdAt: LocalDateTime
) {
    companion object {
        fun from(entity: BidEntity): BidDto = BidDto(
            id = entity.id,
            dealId = entity.dealId,
            userId = entity.userId,
            amount = entity.amount,
            createdAt = entity.createdAt
        )
    }
}
