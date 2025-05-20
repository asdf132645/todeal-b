package com.todeal.domain.push.controller

import com.todeal.domain.push.service.PushService
import org.springframework.web.bind.annotation.*
import com.todeal.domain.push.dto.PushRequest

@RestController
@RequestMapping("/api/push")
class PushController(
    private val pushService: PushService
) {

    @PostMapping("/test")
    fun sendTestNotification(
        @RequestBody request: PushRequest
    ): Map<String, Any> {
        pushService.sendMessageNotification(
            toUserId = request.userId,
            title = request.title,
            body = request.body,
            data = mapOf("customKey" to "customValue")
        )
        return mapOf("success" to true)
    }
}
