package com.todeal.domain.pushNotificationLog.controller

import com.todeal.global.response.ApiResponse
import com.todeal.domain.pushNotificationLog.dto.PushNotificationLogResponse
import com.todeal.domain.pushNotificationLog.repository.PushNotificationLogRepository
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/admin/push-logs")
class PushNotificationLogController(
    private val repository: PushNotificationLogRepository
) {

    @GetMapping("/{userId}")
    fun getUserLogs(@PathVariable userId: Long): ApiResponse<List<PushNotificationLogResponse>> {
        val result = repository.findAllByUserIdOrderByCreatedAtDesc(userId)
            .map { PushNotificationLogResponse.fromEntity(it) }
        return ApiResponse.success(result)
    }
}
