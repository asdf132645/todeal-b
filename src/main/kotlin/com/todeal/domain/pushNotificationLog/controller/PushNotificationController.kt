// PushNotificationLogController.kt
package com.todeal.domain.pushNotificationLog.controller

import com.todeal.domain.pushNotificationLog.dto.PushNotificationLogResponse
import com.todeal.domain.pushNotificationLog.service.PushNotificationLogService
import com.todeal.global.response.ApiResponse
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/push-notifications")
class PushNotificationLogController(
    private val service: PushNotificationLogService
) {
    @GetMapping("/user/{userId}")
    fun getNotifications(@PathVariable userId: Long): ApiResponse<List<PushNotificationLogResponse>> {
        return ApiResponse.success(service.getUserNotifications(userId))
    }
}
