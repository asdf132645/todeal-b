package com.todeal.domain.chat.websocket

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.todeal.domain.chat.dto.ChatMessageRequest
import com.todeal.domain.chat.dto.ChatMessageResponse
import com.todeal.domain.chat.service.ChatService
import org.springframework.stereotype.Component
import org.springframework.web.socket.*
import org.springframework.web.socket.handler.TextWebSocketHandler

@Component
class ChatWebSocketHandler(
    private val chatService: ChatService
) : TextWebSocketHandler() {

    private val sessions = mutableMapOf<Long, MutableList<WebSocketSession>>() // chatRoomId -> sessions

    // ✅ LocalDateTime 직렬화를 위한 모듈 등록
    private val objectMapper: ObjectMapper = ObjectMapper().registerModule(JavaTimeModule())

    override fun afterConnectionEstablished(session: WebSocketSession) {
        val chatRoomId = session.uri?.query?.split("=")?.lastOrNull()?.toLongOrNull()
        if (chatRoomId != null) {
            sessions.computeIfAbsent(chatRoomId) { mutableListOf() }.add(session)
            session.attributes["chatRoomId"] = chatRoomId
        }
    }

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        val chatRoomId = session.attributes["chatRoomId"] as? Long ?: return

        val payloadMap = objectMapper.readValue(message.payload, Map::class.java)
        val type = payloadMap["type"] as? String

        if (type == "typing") {
            broadcastToRoom(chatRoomId, message)
            return
        }

        val senderId = (payloadMap["senderId"] as? Int)?.toLong() ?: return
        val content = payloadMap["message"] as? String ?: return

        // ✅ DB 저장 + 푸시 전송
        val savedMessage: ChatMessageResponse = chatService.sendMessage(
            ChatMessageRequest(
                chatRoomId = chatRoomId,
                senderId = senderId,
                message = content
            )
        )

        // ✅ WebSocket으로 응답 다시 브로드캐스트
        sendMessageToRoom(chatRoomId, savedMessage)
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        val chatRoomId = session.attributes["chatRoomId"] as? Long ?: return
        sessions[chatRoomId]?.remove(session)
    }

    private fun broadcastToRoom(chatRoomId: Long, message: TextMessage) {
        sessions[chatRoomId]?.forEach {
            if (it.isOpen) {
                it.sendMessage(message)
            }
        }
    }

    // ✅ ChatMessageResponse → JSON → WebSocket 전송
    fun sendMessageToRoom(chatRoomId: Long, message: ChatMessageResponse) {
        val payload = objectMapper.writeValueAsString(message)
        val textMessage = TextMessage(payload)
        sessions[chatRoomId]?.forEach {
            if (it.isOpen) {
                it.sendMessage(textMessage)
            }
        }
    }
}
