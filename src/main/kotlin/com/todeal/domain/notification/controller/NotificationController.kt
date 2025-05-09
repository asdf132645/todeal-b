package com.todeal.domain.notification.controller

import com.todeal.global.response.ApiResponse
import com.todeal.domain.notification.dto.NotificationDto
import com.todeal.domain.notification.dto.NotificationResponse
import com.todeal.domain.notification.service.NotificationService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/notifications")
class NotificationController(
    private val notificationService: NotificationService
) {

    @PostMapping
    fun createNotification(@RequestBody dto: NotificationDto): ApiResponse<NotificationResponse> {
        return ApiResponse.success(notificationService.createNotification(dto))
    }

    @GetMapping
    fun getNotifications(@RequestHeader("X-USER-ID") userId: Long): ApiResponse<List<NotificationResponse>> {
        return ApiResponse.success(notificationService.getUserNotifications(userId))
    }

    @PatchMapping("/{id}/read")
    fun markAsRead(@PathVariable id: Long): ApiResponse<String> {
        notificationService.markAsRead(id)
        return ApiResponse.success("읽음 처리 완료")
    }
}
