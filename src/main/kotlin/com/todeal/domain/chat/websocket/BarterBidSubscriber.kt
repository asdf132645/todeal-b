package com.todeal.domain.chat.websocket

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.todeal.global.websocket.WebSocketMessagingService
import org.springframework.data.redis.connection.Message
import org.springframework.data.redis.connection.MessageListener
import org.springframework.stereotype.Component

@Component
class BarterBidSubscriber(
    private val messagingService: WebSocketMessagingService
) : MessageListener {

    private val objectMapper = jacksonObjectMapper()

    override fun onMessage(message: Message, pattern: ByteArray?) {
        val raw = String(message.body)
        val data = objectMapper.readValue(raw, Map::class.java)

        val toUserId = (data["toUserId"] as? String)?.toLongOrNull()
        if (toUserId != null) {
            messagingService.sendToUser(toUserId, raw)
        }
    }
}
