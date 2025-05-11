package com.todeal.domain.chat.dto

import com.todeal.domain.chat.entity.ChatMessageEntity
import java.time.LocalDateTime

data class ChatMessageResponse(
    val id: Long,             // 🔥 메시지 고유 ID
    val chatRoomId: Long,     // 🔥 어떤 채팅방의 메시지인지
    val senderId: Long,
    val message: String,
    val sentAt: LocalDateTime,
    val read: Boolean
) {
    companion object {
        fun fromEntity(entity: ChatMessageEntity): ChatMessageResponse {
            return ChatMessageResponse(
                id = entity.id,
                chatRoomId = entity.chatRoomId,
                senderId = entity.senderId,
                message = entity.message,
                sentAt = entity.sentAt,
                read = entity.read
            )
        }
    }
}
