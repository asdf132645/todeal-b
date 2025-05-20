package com.todeal.domain.chat.websocket

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.todeal.global.websocket.WebSocketMessagingService
import org.springframework.data.redis.connection.Message
import org.springframework.data.redis.connection.MessageListener
import org.springframework.stereotype.Component

@Component
class ChatNotificationSubscriber(
    private val messagingService: WebSocketMessagingService
) : MessageListener {

    private val objectMapper = jacksonObjectMapper()

    override fun onMessage(message: Message, pattern: ByteArray?) {
        try {
            val json = objectMapper.readValue(message.body, Map::class.java)
            val toUserId = (json["receiverId"] ?: return).toString().toLong()
            val payload = objectMapper.writeValueAsString(json)

            println("📨 Redis → WebSocket 전달: userId=$toUserId → $payload")
            messagingService.sendToUser(toUserId, payload)
        } catch (e: Exception) {
            println("❌ Redis 메시지 처리 실패: ${e.message}")
        }
    }
}
