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

    private val roomSessions = mutableMapOf<Long, MutableList<WebSocketSession>>() // chatRoomId -> 세션 목록
    private val userSessions = mutableMapOf<Long, MutableList<WebSocketSession>>() // userId -> 세션 목록

    private val objectMapper = ObjectMapper().registerModule(JavaTimeModule())

    override fun afterConnectionEstablished(session: WebSocketSession) {
        val query = session.uri?.query ?: return
        val params = query.split("&").mapNotNull {
            val parts = it.split("=")
            if (parts.size == 2) parts[0] to parts[1] else null
        }.toMap()

        val chatRoomId = params["chatRoomId"]?.toLongOrNull()
        val userId = params["userId"]?.toLongOrNull()

        println("📡 WebSocket 연결 요청: chatRoomId=$chatRoomId, userId=$userId")

        if (chatRoomId != null) {
            roomSessions.computeIfAbsent(chatRoomId) { mutableListOf() }.add(session)
            session.attributes["chatRoomId"] = chatRoomId
        }

        if (userId != null) {
            userSessions.computeIfAbsent(userId) { mutableListOf() }.add(session)
            session.attributes["userId"] = userId
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

        val savedMessage: ChatMessageResponse = chatService.sendMessage(
            ChatMessageRequest(chatRoomId, senderId, content)
        )

        sendMessageToRoom(chatRoomId, savedMessage)

        // ✅ receiverId null-safe 처리
        savedMessage.receiverId?.let { receiverId ->
            sendMessageToUser(receiverId, savedMessage)
        }
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        roomSessions.values.forEach { it.remove(session) }
        userSessions.values.forEach { it.remove(session) }
    }

    private fun broadcastToRoom(chatRoomId: Long, message: TextMessage) {
        roomSessions[chatRoomId]?.forEach {
            if (it.isOpen) it.sendMessage(message)
        }
    }

    fun sendMessageToRoom(chatRoomId: Long, message: ChatMessageResponse) {
        val json = objectMapper.writeValueAsString(message)
        val textMessage = TextMessage(json)
        roomSessions[chatRoomId]?.forEach {
            if (it.isOpen) it.sendMessage(textMessage)
        }
    }

    fun sendMessageToUser(userId: Long, message: ChatMessageResponse) {
        val json = objectMapper.writeValueAsString(message)
        val textMessage = TextMessage(json)
        userSessions[userId]?.forEach {
            if (it.isOpen) it.sendMessage(textMessage)
        }
    }
}
