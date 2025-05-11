package com.todeal.domain.push.service

import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.Notification
import com.todeal.domain.push.repository.FcmTokenRepository
import org.springframework.stereotype.Service

@Service
class PushService(
    private val fcmTokenRepository: FcmTokenRepository
) {

    fun sendMessageNotification(toUserId: Long, title: String, body: String, data: Map<String, String> = emptyMap()) {
        val token = fcmTokenRepository.findByUserId(toUserId)?.fcmToken ?: return

        val message = Message.builder()
            .setToken(token)
            .setNotification(Notification.builder().setTitle(title).setBody(body).build())
            .putAllData(data)
            .build()

        FirebaseMessaging.getInstance().sendAsync(message)
    }
}
