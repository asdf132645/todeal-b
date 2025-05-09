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
        val fcmToken = redisFcmTokenService.getToken(userId)

        if (fcmToken == null) {
            println("⚠️ FCM 토큰 없음: userId = $userId")
            pushNotificationLogService.save(
                PushNotificationLogDto(
                    userId = userId,
                    title = title,
                    body = body,
                    fcmToken = "N/A",
                    isSuccess = false,
                    responseMessage = "FCM 토큰 없음"
                )
            )
            return
        }

        val message = Message.builder()
            .setToken(fcmToken)
            .setNotification(
                Notification.builder().setTitle(title).setBody(body).build()
            )
            .build()

        try {
            val response = FirebaseMessaging.getInstance().send(message)
            println("📨 FCM 전송 성공: $response")

            pushNotificationLogService.save(
                PushNotificationLogDto(
                    userId = userId,
                    title = title,
                    body = body,
                    fcmToken = fcmToken,
                    isSuccess = true,
                    responseMessage = response
                )
            )

        } catch (e: Exception) {
            println("❌ FCM 전송 실패: ${e.message}")

            pushNotificationLogService.save(
                PushNotificationLogDto(
                    userId = userId,
                    title = title,
                    body = body,
                    fcmToken = fcmToken,
                    isSuccess = false,
                    responseMessage = e.message ?: "Unknown error"
                )
            )
        }
    }
}
