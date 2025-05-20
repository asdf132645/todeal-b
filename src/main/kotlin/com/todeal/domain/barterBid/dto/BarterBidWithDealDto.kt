package com.todeal.domain.bid.dto

import com.todeal.domain.barterBid.entity.BarterBidEntity
import com.todeal.domain.deal.dto.DealInternalDto
import java.time.LocalDateTime

data class BarterBidWithDealDto(
    val id: Long,
    val deal: DealInternalDto, // ✅ 여기 수정
    val proposedItem: String,
    val description: String,
    val createdAt: LocalDateTime,
    val type: String
) {
    companion object {
        fun from(entity: BarterBidEntity, deal: DealInternalDto): BarterBidWithDealDto {
            return BarterBidWithDealDto(
                id = entity.id,
                deal = deal,
                proposedItem = entity.proposedItem,
                description = entity.description,
                createdAt = entity.createdAt,
                type = deal.type
            )
        }
    }
}

