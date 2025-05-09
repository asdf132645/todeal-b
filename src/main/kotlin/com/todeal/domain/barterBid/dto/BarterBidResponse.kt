package com.todeal.domain.barterBid.dto

import com.todeal.domain.barterBid.entity.BarterBidEntity
import java.time.LocalDateTime

data class BarterBidResponse(
    val id: Long,
    val dealId: Long,
    val userId: Long,
    val proposedItem: String,
    val description: String,
    val images: List<String>,
    val createdAt: LocalDateTime
) {
    companion object {
        fun fromEntity(entity: BarterBidEntity) = BarterBidResponse(
            id = entity.id,
            dealId = entity.dealId,
            userId = entity.userId,
            proposedItem = entity.proposedItem,
            description = entity.description,
            images = entity.images,
            createdAt = entity.createdAt
        )
    }
}
