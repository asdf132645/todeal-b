package com.todeal.domain.chat.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "chat_message")
data class ChatMessageEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val chatRoomId: Long,
    val senderId: Long,

    @Column(columnDefinition = "TEXT")
    val message: String,

    var read: Boolean = false,

    val sentAt: LocalDateTime = LocalDateTime.now()
)
