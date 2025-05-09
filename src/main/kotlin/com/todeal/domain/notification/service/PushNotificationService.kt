package com.todeal.domain.notification.service

interface PushNotificationService {
    fun sendPush(userId: Long, title: String, body: String)
}
