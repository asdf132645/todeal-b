// ✅ BidResponse.kt
package com.todeal.domain.bid.dto

import com.todeal.domain.bid.entity.BidEntity
import java.time.LocalDateTime

data class BidResponse(
    val id: Long,
    val dealId: Long,
    val userId: Long,
    val amount: Int,
    val createdAt: LocalDateTime,
    val nickname: String,
    val isWinner: Boolean,
    val evaluated: Boolean
) {
    companion object {
        fun fromEntity(
            entity: BidEntity,
            nickname: String,
            winnerBidId: Long?,
            evaluated: Boolean
        ): BidResponse {
            return BidResponse(
                id = entity.id,
                dealId = entity.dealId,
                userId = entity.userId,
                amount = entity.amount,
                createdAt = entity.createdAt,
                nickname = nickname,
                isWinner = (winnerBidId == entity.id),
                evaluated = evaluated
            )
        }
    }

}
