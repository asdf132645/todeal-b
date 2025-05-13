package com.todeal.global.websocket

import org.springframework.stereotype.Component
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession

@Component
class WebSocketMessagingService {

    private val sessions = mutableMapOf<Long, MutableList<WebSocketSession>>() // userId -> 세션 목록

    /**
     * WebSocket 연결 시 세션 등록
     */
    fun register(userId: Long, session: WebSocketSession) {
        sessions.computeIfAbsent(userId) { mutableListOf() }.add(session)
        println("✅ WebSocket 세션 등록됨: userId=$userId / session=$session")
    }

    /**
     * WebSocket 연결 종료 시 세션 제거
     */
    fun unregister(userId: Long, session: WebSocketSession) {
        sessions[userId]?.remove(session)
        println("🧹 WebSocket 세션 제거됨: userId=$userId / session=$session")
    }

    /**
     * 특정 유저에게 메시지 전송
     */
    fun sendToUser(userId: Long, message: String) {
        val userSessions = sessions[userId]

        if (userSessions.isNullOrEmpty()) {
            println("❌ WebSocket 세션 없음: userId=$userId")
            return
        }

        println("🚀 WebSocket 알림 전송 시도 → userId=$userId :: $message")

        // 유효한 세션만 필터링
        val validSessions = userSessions.filter { it != null && it.isOpen }.toMutableList()
        val failedSessions = mutableListOf<WebSocketSession>()

        validSessions.forEach { session ->
            try {
                session.sendMessage(TextMessage(message))
                println("✅ WebSocket 전송 성공: userId=$userId")
            } catch (e: Exception) {
                println("❌ WebSocket 전송 실패: userId=$userId / 이유: ${e.message}")
                failedSessions.add(session)
            }
        }

        // 실패하거나 닫힌 세션 정리
        val cleanedSessions = validSessions - failedSessions
        sessions[userId] = cleanedSessions.toMutableList()
        if (cleanedSessions.isEmpty()) {
            sessions.remove(userId)
        }
    }
}
