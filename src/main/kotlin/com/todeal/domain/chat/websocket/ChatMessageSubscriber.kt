package com.todeal.domain.chat.websocket

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.todeal.domain.chat.dto.ChatMessageResponse
import org.springframework.data.redis.connection.Message
import org.springframework.data.redis.connection.MessageListener
import org.springframework.stereotype.Component

@Component
class ChatMessageSubscriber(
    private val chatWebSocketHandler: ChatWebSocketHandler
) : MessageListener {

    private val objectMapper = ObjectMapper().registerModule(JavaTimeModule())

    override fun onMessage(message: Message, pattern: ByteArray?) {
        val json = message.body.toString(Charsets.UTF_8)
        val chatMessage = objectMapper.readValue(json, ChatMessageResponse::class.java)

        // 1. 채팅방 사용자들에게 전송
        chatWebSocketHandler.sendMessageToRoom(chatMessage.chatRoomId, chatMessage)

        // 2. 수신자에게 알림 전송 (UI 띄우기용)
        if (chatMessage.receiverId != null) {
            chatWebSocketHandler.sendMessageToUser(chatMessage.receiverId, chatMessage)
        }
    }
}
