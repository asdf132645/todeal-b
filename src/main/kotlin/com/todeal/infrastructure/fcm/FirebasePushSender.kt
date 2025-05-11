package com.todeal.infrastructure.fcm

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.Notification
import com.todeal.domain.notification.service.PushNotificationService
import com.todeal.domain.pushNotificationLog.dto.PushNotificationLogDto
import com.todeal.domain.pushNotificationLog.service.PushNotificationLogService
import com.todeal.infrastructure.redis.RedisFcmTokenService
import org.springframework.stereotype.Service

@Service
class FirebasePushSender(
    private val credentialLoader: FirebaseCredentialLoader,
    private val pushNotificationLogService: PushNotificationLogService,
    private val redisFcmTokenService: RedisFcmTokenService
) : PushNotificationService {

    init {
        if (FirebaseApp.getApps().isEmpty()) {
            val serviceAccount = credentialLoader.load()

            val options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build()

            FirebaseApp.initializeApp(options)
            println("✅ Firebase 초기화 완료")
        }
    }

    override fun sendPush(userId: Long, title: String, body: String) {
        sendPush(userId, title, body, emptyMap())
    }

    fun sendPush(userId: Long, title: String, body: String, data: Map<String, String>) {
        val fcmToken = redisFcmTokenService.getToken(userId)

        if (fcmToken == null) {
            logFailure(userId, title, body, "N/A", "FCM 토큰 없음")
            return
        }

        val message = Message.builder()
            .setToken(fcmToken)
            .setNotification(Notification.builder().setTitle(title).setBody(body).build())
            .putAllData(data)
            .build()

        try {
            val response = FirebaseMessaging.getInstance().send(message)
            println("📨 FCM 전송 성공: $response")
            logSuccess(userId, title, body, fcmToken, response)

        } catch (e: Exception) {
            println("❌ FCM 전송 실패: ${e.message}")
            logFailure(userId, title, body, fcmToken, e.message ?: "Unknown error")
        }
    }

    private fun logSuccess(userId: Long, title: String, body: String, token: String, response: String) {
        pushNotificationLogService.save(
            PushNotificationLogDto(
                userId = userId,
                title = title,
                body = body,
                fcmToken = token,
                isSuccess = true,
                responseMessage = response
            )
        )
    }

    private fun logFailure(userId: Long, title: String, body: String, token: String, error: String) {
        pushNotificationLogService.save(
            PushNotificationLogDto(
                userId = userId,
                title = title,
                body = body,
                fcmToken = token,
                isSuccess = false,
                responseMessage = error
            )
        )
    }
}
