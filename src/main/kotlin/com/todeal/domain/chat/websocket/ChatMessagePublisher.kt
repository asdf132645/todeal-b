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
}
