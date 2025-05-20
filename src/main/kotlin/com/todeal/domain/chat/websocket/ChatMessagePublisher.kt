package com.todeal.domain.chat.websocket

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component

@Component
class ChatMessagePublisher(
    private val redisTemplate: StringRedisTemplate
) {
    fun publish(message: String) {
        println("📤 Redis 발행됨: $message")
        redisTemplate.convertAndSend("pubsub:chat:message", message)
    }

    fun publishToChatRoom(chatRoomId: Long, payload: String) {
        redisTemplate.convertAndSend("pubsub:chat:room:$chatRoomId", payload)  // 실시간 채팅방용 (있다면)
    }

    fun publishToNotifyChannel(payload: String) {
        redisTemplate.convertAndSend("pubsub:chat:message", payload) // 웹뷰 알림용
    }

}
