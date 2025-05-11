package com.todeal.domain.chat.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "chat_room")
data class ChatRoomEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val dealId: Long,
    val sellerId: Long,
    val buyerId: Long,

    val createdAt: LocalDateTime = LocalDateTime.now()
)
