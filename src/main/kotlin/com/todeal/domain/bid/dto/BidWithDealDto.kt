// ✅ 파일 위치: com.todeal.domain.bid.dto.BidWithDealDto.kt
package com.todeal.domain.bid.dto

import com.todeal.domain.bid.entity.BidEntity
import com.todeal.domain.deal.dto.DealInternalDto
import java.time.LocalDateTime

data class BidWithDealDto(
    val id: Long,
    val deal: DealInternalDto, // ✅ 여기 수정
    val nickname: String,
    val amount: Int,
    val createdAt: LocalDateTime,
    val type: String, // "used"
    val dealOwnerId: Long
) {
    companion object {
        fun from(entity: BidEntity, nickname: String, deal: DealInternalDto): BidWithDealDto {
            return BidWithDealDto(
                id = entity.id,
                deal = deal,
                nickname = nickname,
                amount = entity.amount,
                createdAt = entity.createdAt,
                type = deal.type,
                dealOwnerId = deal.ownerId
            )
        }
    }
}
