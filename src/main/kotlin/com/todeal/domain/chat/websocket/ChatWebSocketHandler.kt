package com.todeal.domain.chat.websocket

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.todeal.domain.chat.dto.ChatMessageRequest
import com.todeal.domain.chat.dto.ChatMessageResponse
import com.todeal.domain.chat.service.ChatService
import com.todeal.global.websocket.WebSocketMessagingService
import org.springframework.stereotype.Component
import org.springframework.web.socket.*
import org.springframework.web.socket.handler.TextWebSocketHandler

@Component
class ChatWebSocketHandler(
    private val chatService: ChatService,
    private val messagingService: WebSocketMessagingService
) : TextWebSocketHandler() {

    private val roomSessions = mutableMapOf<Long, MutableList<WebSocketSession>>() // chatRoomId -> 세션 목록
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
            messagingService.register(userId, session)
            session.attributes["userId"] = userId
        }
    }

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        val chatRoomId = session.attributes["chatRoomId"] as? Long ?: return

        val payloadMap = objectMapper.readValue(message.payload, Map::class.java)
        println("📩 받은 메시지 전체 payload: ${message.payload}")
        val type = payloadMap["type"] as? String
        println("📩 WebSocket payloadMap: $payloadMap")

        // ✅ typing 이벤트만 브로드캐스트
        if (type == "typing") {
            broadcastToRoom(chatRoomId, message)
            return
        }

        val senderId = when (val raw = payloadMap["senderId"]) {
            is Int -> raw.toLong()
            is Long -> raw
            is Double -> raw.toLong()
            is String -> raw.toLongOrNull()
            else -> null
        } ?: run {
            println("❌ senderId 파싱 실패: ${payloadMap["senderId"]}")
            return
        }

        val content = payloadMap["message"] as? String ?: return

        val savedMessage: ChatMessageResponse = chatService.sendMessage(
            ChatMessageRequest(chatRoomId, senderId, content)
        )

        // 🔥 WebSocket 세션에 바로 전송
        println("📡 WebSocket 세션 직접 전송 시작")
        sendMessageToRoom(chatRoomId, savedMessage)

        // 🔕 송신자와 수신자 모두 방에 접속해 있으면 알림 생략
        savedMessage.receiverId?.let { receiverId ->
            val roomUsers = roomSessions[chatRoomId]
                ?.mapNotNull { it.attributes["userId"] as? Long }
                ?.toSet() ?: emptySet()

            val bothOnline = senderId in roomUsers && receiverId in roomUsers

            if (!bothOnline) {
                println("📣 수신자에게 알림 보냄: userId=$receiverId")
                sendMessageToUser(receiverId, savedMessage)
            } else {
                println("🔕 송신자/수신자 모두 접속 중 → 알림 생략")
            }
        }
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        roomSessions.values.forEach { it.remove(session) }

        val userId = session.attributes["userId"] as? Long
        if (userId != null) {
            messagingService.unregister(userId, session)
        }
    }

    private fun broadcastToRoom(chatRoomId: Long, message: TextMessage) {
        roomSessions[chatRoomId]?.forEach {
            if (it.isOpen) it.sendMessage(message)
        }
    }

    fun sendMessageToRoom(chatRoomId: Long, message: ChatMessageResponse) {
        val json = objectMapper.writeValueAsString(message)
        val textMessage = TextMessage(json)

        val sessions = roomSessions[chatRoomId]
        println("📦 sendMessageToRoom → 세션 수: ${sessions?.size ?: 0}, message=$json")

        sessions?.forEach {
            val userId = it.attributes["userId"]
            println("👉 전송 대상: userId=$userId, isOpen=${it.isOpen}")
            if (it.isOpen) it.sendMessage(textMessage)
        }
    }


    fun sendMessageToRoom(chatRoomId: Long, messageJson: String) {
        val textMessage = TextMessage(messageJson)
        roomSessions[chatRoomId]?.forEach {
            if (it.isOpen) it.sendMessage(textMessage)
        }
    }

    fun sendMessageToUser(userId: Long, message: ChatMessageResponse) {
        val json = objectMapper.writeValueAsString(message)
        messagingService.sendToUser(userId, json)
    }

    fun sendMessageToUser(userId: Long, messageJson: String) {
        println("📤 WebSocket → userId=$userId, message=$messageJson")
        messagingService.sendToUser(userId, messageJson)
    }
}
