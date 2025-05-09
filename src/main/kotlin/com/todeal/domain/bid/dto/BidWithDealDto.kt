// ✅ 파일 위치: com.todeal.domain.bid.dto.BidWithDealDto.kt
package com.todeal.domain.bid.dto

import com.todeal.domain.bid.entity.BidEntity
import com.todeal.domain.deal.dto.DealResponse

data class BidWithDealDto(
    val id: Long,
    val deal: DealResponse,
    val amount: Int,
    val nickname: String,
    val createdAt: String
) {
    companion object {
        fun from(entity: BidEntity, nickname: String, deal: DealResponse): BidWithDealDto {
            return BidWithDealDto(
                id = entity.id,
                deal = deal,
                amount = entity.amount,
                nickname = nickname,
                createdAt = entity.createdAt.toString()
            )
        }
    }
}