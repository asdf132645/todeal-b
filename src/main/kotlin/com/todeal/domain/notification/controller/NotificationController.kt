// controller/NotificationController.kt
package com.todeal.domain.notification.controller

import com.todeal.domain.notification.dto.*
import com.todeal.domain.notification.service.NotificationService
import com.todeal.global.response.ApiResponse
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/notifications")
class NotificationController(
    private val notificationService: NotificationService
) {
    @PostMapping
    fun create(@RequestBody request: NotificationDto): ApiResponse<NotificationDto> {
        return ApiResponse.success(notificationService.create(request))
    }

    @GetMapping("/user/{userId}")
    fun getByUser(@PathVariable userId: Long): ApiResponse<List<NotificationDto>> {
        return ApiResponse.success(notificationService.getByUser(userId))
    }

    @PutMapping("/{id}/read")
    fun markAsRead(@PathVariable id: Long): ApiResponse<NotificationDto> {
        return ApiResponse.success(notificationService.markAsRead(id))
    }
}
