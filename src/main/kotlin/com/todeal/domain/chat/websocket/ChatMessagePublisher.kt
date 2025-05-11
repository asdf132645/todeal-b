package com.todeal.domain.chat.websocket

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component

@Component
class ChatMessagePublisher(
    private val redisTemplate: StringRedisTemplate
) {
    fun publish(message: String) {
        redisTemplate.convertAndSend("pubsub:chat:message", message)
    }
}
