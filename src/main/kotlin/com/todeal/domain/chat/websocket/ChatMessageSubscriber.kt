package com.todeal.domain.chat.websocket

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
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
        println("📨 Redis 수신됨 → $json")

        val node = objectMapper.readTree(json)
        val receiverId = node["receiverId"]?.asLong()
        val chatRoomId = node["chatRoomId"]?.asLong()

        if (chatRoomId != null) {
            chatWebSocketHandler.sendMessageToRoom(chatRoomId, json)
        }

        if (receiverId != null) {
            chatWebSocketHandler.sendMessageToUser(receiverId, json)
        }
    }
}
