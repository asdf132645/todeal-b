package com.todeal.domain.chat.dto

import com.todeal.domain.chat.entity.ChatMessageEntity
import java.time.LocalDateTime

data class ChatMessageResponse(
    val id: Long,
    val chatRoomId: Long,
    val senderId: Long,
    val message: String,
    val sentAt: LocalDateTime,
    val read: Boolean,
    val receiverId: Long? = null
) {
    companion object {
        fun fromEntity(entity: ChatMessageEntity): ChatMessageResponse {
            return ChatMessageResponse(
                id = entity.id!!,
                chatRoomId = entity.chatRoomId,
                senderId = entity.senderId,
                message = entity.message,
                sentAt = entity.sentAt,
                read = entity.read,
                receiverId = null
            )
        }
    }
}
