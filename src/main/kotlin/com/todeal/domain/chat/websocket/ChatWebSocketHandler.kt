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
        val senderId = session.attributes["userId"] as? Long ?: run {
            println("❌ userId 없음 - 세션 종료")
            session.close(CloseStatus.NOT_ACCEPTABLE)
            return
        }

        val payloadMap = objectMapper.readValue(message.payload, Map::class.java)
        val type = payloadMap["type"] as? String
        val content = payloadMap["message"] as? String ?: return

        println("📩 받은 메시지 payload: $payloadMap")

        if (type == "typing") {
            broadcastToRoom(chatRoomId, message)
            return
        }

        val savedMessage = chatService.sendMessage(
            ChatMessageRequest(chatRoomId, senderId, content),
            authenticatedUserId = senderId
        )

        // ✅ 1. 채팅방 사용자들에게만 WebSocket 전송
        sendMessageToRoom(chatRoomId, savedMessage)

        // ✅ 2. 상대방이 방에 없을 때만 알림용 전송
        savedMessage.receiverId?.let { receiverId ->
            if (receiverId == senderId) return@let // 💥 자기자신이면 푸시 생략

            val roomUsers = roomSessions[chatRoomId]
                ?.mapNotNull { it.attributes["userId"] as? Long }
                ?.toSet() ?: emptySet()

            val bothOnline = senderId in roomUsers && receiverId in roomUsers
            if (!bothOnline) {
                val notifyPayload = objectMapper.writeValueAsString(
                    mapOf(
                        "type" to "chat",
                        "chatRoomId" to savedMessage.chatRoomId,
                        "message" to savedMessage.message,
                        "senderId" to savedMessage.senderId,
                        "receiverId" to savedMessage.receiverId,
                        "sentAt" to savedMessage.sentAt.toString(),
                        "source" to "notify"
                    )
                )
                println("📣 알림 WebSocket 전송 to $receiverId")
                sendMessageToUser(receiverId, notifyPayload)
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
