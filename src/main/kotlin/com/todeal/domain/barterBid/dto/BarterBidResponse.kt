package com.todeal.domain.barterBid.dto

import com.todeal.domain.barterBid.entity.BarterBidEntity
import java.time.LocalDateTime

data class BarterBidResponse(
    val id: Long,
    val dealId: Long,
    val userId: Long,
    val nickname: String, // ✅ 추가됨
    val proposedItem: String,
    val description: String,
    val images: List<String>,
    val createdAt: LocalDateTime
) {
    companion object {
        fun fromEntity(entity: BarterBidEntity, nickname: String) = BarterBidResponse(
            id = entity.id,
            dealId = entity.dealId,
            userId = entity.userId,
            nickname = nickname,
            proposedItem = entity.proposedItem,
            description = entity.description,
            images = entity.images,
            createdAt = entity.createdAt
        )
    }
}
