package com.todeal.domain.chat.dto

import com.todeal.domain.chat.entity.ChatRoomEntity
import java.time.LocalDateTime

data class ChatRoomResponse(
    val id: Long,
    val dealId: Long,
    val sellerId: Long,
    val buyerId: Long,
    val createdAt: LocalDateTime
) {
    companion object {
        fun fromEntity(entity: ChatRoomEntity): ChatRoomResponse {
            return ChatRoomResponse(
                id = entity.id,
                dealId = entity.dealId,
                sellerId = entity.sellerId,
                buyerId = entity.buyerId,
                createdAt = entity.createdAt
            )
        }
    }
}
