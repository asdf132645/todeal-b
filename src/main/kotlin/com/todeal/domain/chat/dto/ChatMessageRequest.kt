package com.todeal.domain.chat.dto

data class ChatMessageRequest(
    val chatRoomId: Long,
    val senderId: Long,
    val message: String
)
