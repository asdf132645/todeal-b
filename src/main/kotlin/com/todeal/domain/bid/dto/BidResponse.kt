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
    val isWinner: Boolean // ✅ 추가
) {
    companion object {
        fun fromEntity(entity: BidEntity, nickname: String, winnerBidId: Long?): BidResponse {
            return BidResponse(
                id = entity.id,
                dealId = entity.dealId,
                userId = entity.userId,
                amount = entity.amount,
                createdAt = entity.createdAt,
                nickname = nickname,
                isWinner = (winnerBidId == entity.id) // ✅ 낙찰 여부 판단
            )
        }
    }
}
