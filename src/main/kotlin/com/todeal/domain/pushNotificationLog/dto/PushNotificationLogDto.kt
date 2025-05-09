package com.todeal.domain.pushNotificationLog.dto

data class PushNotificationLogDto(
    val userId: Long,
    val title: String,
    val body: String,
    val fcmToken: String,
    val isSuccess: Boolean,
    val responseMessage: String?
)
