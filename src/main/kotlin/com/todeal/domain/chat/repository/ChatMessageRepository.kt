package com.todeal.domain.chat.repository

import com.todeal.domain.chat.entity.ChatMessageEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface ChatMessageRepository : JpaRepository<ChatMessageEntity, Long> {

    fun findAllByChatRoomIdOrderBySentAtAsc(chatRoomId: Long): List<ChatMessageEntity>

    @Query(
        """
        SELECT c FROM ChatMessageEntity c
        WHERE c.chatRoomId = :chatRoomId AND c.id < :lastMessageId
        ORDER BY c.id DESC
    """
    )
    fun findTopByChatRoomIdAndIdLessThanOrderByIdDesc(
        @Param("chatRoomId") chatRoomId: Long,
        @Param("lastMessageId") lastMessageId: Long,
        @Param("limit") limit: Int
    ): List<ChatMessageEntity>

    @Query(
        """
        SELECT c FROM ChatMessageEntity c
        WHERE c.chatRoomId = :chatRoomId
        ORDER BY c.id DESC
    """
    )
    fun findTopByChatRoomIdOrderByIdDesc(
        @Param("chatRoomId") chatRoomId: Long,
        @Param("limit") limit: Int
    ): List<ChatMessageEntity>

    fun deleteByChatRoomId(chatRoomId: Long)
}
