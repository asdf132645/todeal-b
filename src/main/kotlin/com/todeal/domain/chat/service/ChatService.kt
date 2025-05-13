package com.todeal.domain.chat.service

import com.todeal.domain.chat.dto.*
import com.todeal.domain.chat.entity.ChatMessageEntity
import com.todeal.domain.chat.entity.ChatRoomEntity
import com.todeal.domain.chat.repository.ChatMessageRepository
import com.todeal.domain.chat.repository.ChatRoomRepository
import com.todeal.domain.push.service.PushService
import org.springframework.data.domain.PageRequest
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
        val pageable = PageRequest.of(0, limit)
        val messages = if (lastMessageId != null) {
            chatMessageRepository.findByChatRoomIdAndIdLessThanOrderByIdDesc(chatRoomId, lastMessageId, pageable)
        } else {
            chatMessageRepository.findByChatRoomIdOrderByIdDesc(chatRoomId, pageable)
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

        // 🔔 푸시 및 실시간 알림 전송을 위한 receiverId 추출
        val chatRoom = chatRoomRepository.findByIdOrNull(request.chatRoomId)
        val receiverId = if (chatRoom?.sellerId == request.senderId) chatRoom.buyerId else chatRoom?.sellerId

        if (receiverId != null) {
            pushService.sendMessageNotification(
                toUserId = receiverId,
                title = "새 메시지 도착",
                body = response.message,
                data = mapOf("chatRoomId" to request.chatRoomId.toString())
            )
            return response.copy(receiverId = receiverId) // ✅ receiverId 포함해서 반환
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