package com.todeal.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.todeal.domain.chat.websocket.BarterBidSubscriber
import com.todeal.domain.chat.websocket.BidSubscriber
import com.todeal.domain.chat.websocket.ChatMessageSubscriber
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.listener.ChannelTopic
import org.springframework.data.redis.listener.RedisMessageListenerContainer
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer
import org.springframework.data.redis.core.StringRedisTemplate

@Configuration
class RedisConfig {

    @Bean
    fun redisConnectionFactory(): RedisConnectionFactory = LettuceConnectionFactory("localhost", 6379)

    @Bean
    fun redisTemplate(): RedisTemplate<String, Any> {
        val template = RedisTemplate<String, Any>()
        template.setConnectionFactory(redisConnectionFactory())

        val objectMapper = ObjectMapper()
            .registerModule(JavaTimeModule())
            .findAndRegisterModules()

        val serializer = GenericJackson2JsonRedisSerializer(objectMapper)

        template.keySerializer = StringRedisSerializer()
        template.valueSerializer = serializer
        template.hashKeySerializer = StringRedisSerializer()
        template.hashValueSerializer = serializer
        template.afterPropertiesSet()
        return template
    }

    @Bean
    fun stringRedisTemplate(connectionFactory: RedisConnectionFactory): StringRedisTemplate =
        StringRedisTemplate(connectionFactory)

    @Bean
    fun redisMessageListenerContainer(
        connectionFactory: RedisConnectionFactory,
        bidSubscriber: BidSubscriber,
        barterBidSubscriber: BarterBidSubscriber,
        chatMessageSubscriber: ChatMessageSubscriber
    ): RedisMessageListenerContainer {
        println("✅ RedisMessageListenerContainer 등록됨")
        val container = RedisMessageListenerContainer()
        container.setConnectionFactory(connectionFactory)
        container.addMessageListener(bidSubscriber, ChannelTopic("pubsub:bid:new"))
        container.addMessageListener(barterBidSubscriber, ChannelTopic("pubsub:barter:new"))
        container.addMessageListener(chatMessageSubscriber, ChannelTopic("pubsub:chat:message"))
        return container
    }
}
