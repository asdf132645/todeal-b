package com.todeal.global.config

import com.todeal.domain.chat.websocket.ChatNotificationSubscriber
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.listener.PatternTopic
import org.springframework.data.redis.listener.RedisMessageListenerContainer

@Configuration
class RedisSubscriberConfig {

    @Bean
    fun redisContainer(
        connectionFactory: RedisConnectionFactory,
        chatNotificationSubscriber: ChatNotificationSubscriber
    ): RedisMessageListenerContainer {
        val container = RedisMessageListenerContainer()
        container.setConnectionFactory(connectionFactory)
        container.addMessageListener(
            chatNotificationSubscriber,
            PatternTopic("pubsub:chat:message") // 이 채널명과 publish하는 채널명이 일치해야 함
        )
        return container
    }
}
