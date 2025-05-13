package com.todeal.domain.notification.websocket

import com.todeal.global.websocket.WebSocketMessagingService
import org.springframework.stereotype.Component
import org.springframework.web.socket.*
import org.springframework.web.socket.handler.TextWebSocketHandler

@Component
class NotificationWebSocketHandler(
    private val messagingService: WebSocketMessagingService
) : TextWebSocketHandler() {

    override fun afterConnectionEstablished(session: WebSocketSession) {
        val query = session.uri?.query ?: return

        // 안전하게 파싱
        val params = query.split("&").mapNotNull {
            val parts = it.split("=")
            if (parts.size == 2) parts[0] to parts[1] else null
        }.toMap()

        val userId = params["userId"]?.toLongOrNull()

        if (userId != null) {
            session.attributes["userId"] = userId
            messagingService.register(userId, session)
            println("✅ 알림 WebSocket 연결됨: userId=$userId")
        } else {
            println("⚠️ WebSocket 연결 실패: userId 파라미터 없음 또는 변환 실패")
        }
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        val userId = session.attributes["userId"] as? Long
        if (userId != null) {
            messagingService.unregister(userId, session)
            println("🔌 알림 WebSocket 종료됨: userId=$userId")
        } else {
            println("⚠️ 세션 종료 시 userId 누락됨")
        }
    }

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        // 알림 WebSocket은 수신 전용이므로 처리 없음
        println("⚠️ 클라이언트 → 서버 메시지 수신됨 (무시): ${message.payload}")
    }
}
