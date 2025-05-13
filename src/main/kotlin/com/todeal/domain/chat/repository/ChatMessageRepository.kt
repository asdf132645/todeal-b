package com.todeal.domain.chat.repository

import com.todeal.domain.chat.entity.ChatMessageEntity
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface ChatMessageRepository : JpaRepository<ChatMessageEntity, Long> {

    fun findAllByChatRoomIdOrderBySentAtAsc(chatRoomId: Long): List<ChatMessageEntity>

    fun findByChatRoomIdAndIdLessThanOrderByIdDesc(
        chatRoomId: Long,
        lastMessageId: Long,
        pageable: Pageable
    ): List<ChatMessageEntity>

    fun findByChatRoomIdOrderByIdDesc(
        chatRoomId: Long,
        pageable: Pageable
    ): List<ChatMessageEntity>

    fun deleteByChatRoomId(chatRoomId: Long)
}
