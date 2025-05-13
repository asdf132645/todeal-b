package com.todeal.global.config

import com.todeal.domain.chat.websocket.ChatWebSocketHandler
import com.todeal.domain.notification.websocket.NotificationWebSocketHandler
import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry

@Configuration
@EnableWebSocket
class WebSocketConfig(
    private val chatWebSocketHandler: ChatWebSocketHandler,
    private val notificationWebSocketHandler: NotificationWebSocketHandler
) : WebSocketConfigurer {

    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        registry.addHandler(chatWebSocketHandler, "/ws/chat")
            .setAllowedOrigins("*") // 실제 운영시 origin 제한 필요

        registry.addHandler(notificationWebSocketHandler, "/ws/notify")
            .setAllowedOrigins("*") // 알림 전용 WebSocket 엔드포인트
    }
}
