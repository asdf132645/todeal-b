package com.todeal.domain.chat.service

import com.todeal.domain.chat.dto.*
import com.todeal.domain.chat.entity.ChatMessageEntity
import com.todeal.domain.chat.entity.ChatRoomEntity
import com.todeal.domain.chat.repository.ChatMessageRepository
import com.todeal.domain.chat.repository.ChatRoomRepository
import com.todeal.domain.push.service.PushService
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ChatService(
    private val chatRoomRepository: ChatRoomRepository,
    private val chatMessageRepository: ChatMessageRepository,
    private val pushService: PushService
) {

    fun createChatRoom(dto: ChatRoomDto): ChatRoomResponse {
        val saved = chatRoomRepository.save(
            ChatRoomEntity(
                dealId = dto.dealId,
                sellerId = dto.sellerId,
                buyerId = dto.buyerId
            )
        )
        return ChatRoomResponse.fromEntity(saved)
    }

    fun getUserChatRooms(userId: Long): List<ChatRoomResponse> {
        return chatRoomRepository.findAllBySellerIdOrBuyerId(userId, userId)
            .map { ChatRoomResponse.fromEntity(it) }
    }

    fun getRecentMessages(chatRoomId: Long, lastMessageId: Long?, limit: Int): List<ChatMessageResponse> {
        val messages = if (lastMessageId != null) {
            chatMessageRepository.findTopByChatRoomIdAndIdLessThanOrderByIdDesc(chatRoomId, lastMessageId, limit)
        } else {
            chatMessageRepository.findTopByChatRoomIdOrderByIdDesc(chatRoomId, limit)
        }
        return messages.reversed().map { ChatMessageResponse.fromEntity(it) }
    }

    @Transactional
    fun sendMessage(request: ChatMessageRequest): ChatMessageResponse {
        val entity = chatMessageRepository.save(
            ChatMessageEntity(
                chatRoomId = request.chatRoomId,
                senderId = request.senderId,
                message = request.message
            )
        )
        val response = ChatMessageResponse.fromEntity(entity)

        // 🔔 푸시 알림 전송
        val chatRoom = chatRoomRepository.findByIdOrNull(request.chatRoomId)
        val receiverId = if (chatRoom?.sellerId == request.senderId) chatRoom.buyerId else chatRoom?.sellerId
        if (receiverId != null) {
            pushService.sendMessageNotification(
                toUserId = receiverId,
                title = "새 메시지 도착",
                body = response.message,
                data = mapOf("chatRoomId" to request.chatRoomId.toString())
            )
        }

        return response
    }

    @Transactional
    fun markMessagesAsRead(chatRoomId: Long, readerId: Long) {
        val unreadMessages = chatMessageRepository.findAllByChatRoomIdOrderBySentAtAsc(chatRoomId)
            .filter { it.senderId != readerId && !it.read }

        unreadMessages.forEach { it.read = true }
        chatMessageRepository.saveAll(unreadMessages)
    }
}