package com.todeal.domain.chat.dto

data class ReadReceiptRequest(
    val chatRoomId: Long,
    val userId: Long
)
