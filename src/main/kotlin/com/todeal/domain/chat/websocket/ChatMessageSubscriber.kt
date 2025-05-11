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

    // ✅ LocalDateTime 지원을 위한 모듈 등록
    private val objectMapper = ObjectMapper().registerModule(JavaTimeModule())

    override fun onMessage(message: Message, pattern: ByteArray?) {
        val json = message.body.toString(Charsets.UTF_8)

        // ✅ JSON 문자열을 ChatMessageResponse 객체로 변환
        val chatMessage = objectMapper.readValue(json, ChatMessageResponse::class.java)

        // ✅ WebSocket으로 해당 방에 메시지 전송
        chatWebSocketHandler.sendMessageToRoom(chatMessage.chatRoomId, chatMessage)
    }
}
