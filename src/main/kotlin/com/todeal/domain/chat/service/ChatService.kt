package com.todeal.domain.chat.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.todeal.domain.chat.dto.*
import com.todeal.domain.chat.entity.ChatMessageEntity
import com.todeal.domain.chat.entity.ChatRoomEntity
import com.todeal.domain.chat.repository.ChatMessageRepository
import com.todeal.domain.chat.repository.ChatRoomRepository
import com.todeal.domain.chat.websocket.ChatMessagePublisher
import com.todeal.domain.push.service.PushService
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ChatService(
    private val chatRoomRepository: ChatRoomRepository,
    private val chatMessageRepository: ChatMessageRepository,
    private val pushService: PushService,
    private val chatMessagePublisher: ChatMessagePublisher
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

    fun sendMessage(request: ChatMessageRequest, authenticatedUserId: Long): ChatMessageResponse {
        val chatRoom = chatRoomRepository.findByIdOrNull(request.chatRoomId)
            ?: throw IllegalArgumentException("존재하지 않는 채팅방입니다.")

        // 🛡️ senderId는 요청에서 받지 않고, JWT 인증된 사용자 ID로 강제 설정
        val senderId = authenticatedUserId

        val receiverId = when (senderId) {
            chatRoom.sellerId -> chatRoom.buyerId
            chatRoom.buyerId -> chatRoom.sellerId
            else -> throw IllegalArgumentException("채팅방에 속하지 않은 사용자입니다.")
        }

        val entity = chatMessageRepository.save(
            ChatMessageEntity(
                chatRoomId = request.chatRoomId,
                senderId = senderId, // ✅ 안전하게 설정됨
                message = request.message
            )
        )

        val response = ChatMessageResponse.fromEntity(entity)

        // 🔔 FCM 푸시
        pushService.sendMessageNotification(
            toUserId = receiverId,
            title = "새 메시지 도착",
            body = response.message,
            data = mapOf("chatRoomId" to request.chatRoomId.toString())
        )

        // 🔁 Redis 발행
        val payload = mapOf(
            "type" to "chat",
            "chatRoomId" to request.chatRoomId,
            "message" to response.message,
            "senderId" to senderId,
            "receiverId" to receiverId,
            "sentAt" to response.sentAt.toString()
        )

        val chatNotiPayload = mapOf(
            "type" to "chatNoti",
            "chatRoomId" to request.chatRoomId,
            "message" to response.message,
            "senderId" to senderId,
            "receiverId" to receiverId,
            "sentAt" to response.sentAt.toString()
        )

        val objectMapper = jacksonObjectMapper()
        chatMessagePublisher.publishToChatRoom(request.chatRoomId, objectMapper.writeValueAsString(payload))
        chatMessagePublisher.publishToNotifyChannel(objectMapper.writeValueAsString(chatNotiPayload))

        return response.copy(receiverId = receiverId)
    }


    @Transactional
    fun markMessagesAsRead(chatRoomId: Long, readerId: Long) {
        val unreadMessages = chatMessageRepository.findAllByChatRoomIdOrderBySentAtAsc(chatRoomId)
            .filter { it.senderId != readerId && !it.read }

        unreadMessages.forEach { it.read = true }
        chatMessageRepository.saveAll(unreadMessages)
    }

    fun getExistingChatRoom(userId1: Long, userId2: Long, dealId: Long): ChatRoomResponse? {
        val room = chatRoomRepository.findByUsersAndDeal(userId1, userId2, dealId)
        return room?.let { ChatRoomResponse.fromEntity(it) }
    }

}