package com.todeal.domain.chat.websocket

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.todeal.global.websocket.WebSocketMessagingService
import org.springframework.data.redis.connection.Message
import org.springframework.data.redis.connection.MessageListener
import org.springframework.stereotype.Component

@Component
class BidSubscriber(
    private val messagingService: WebSocketMessagingService
) : MessageListener {

    private val objectMapper = jacksonObjectMapper()

    override fun onMessage(message: Message, pattern: ByteArray?) {
        println("✅ Redis 메시지 수신 시도: ${String(message.body)}")

        val raw = String(message.body)
        val data = objectMapper.readValue(raw, Map::class.java)

        val toUserId = when (val rawUserId = data["toUserId"]) {
            is Number -> rawUserId.toLong()
            is String -> rawUserId.toLongOrNull()
            else -> null
        }

        if (toUserId != null) {
            println("🚀 WebSocket 알림 전송 대상 userId=$toUserId")
            messagingService.sendToUser(toUserId, raw)
        } else {
            println("❌ toUserId 파싱 실패: ${data["toUserId"]}")
        }
    }

}
