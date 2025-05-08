// ✅ UserLocationController.kt
package com.todeal.domain.userlocation

import com.todeal.global.response.ApiResponse
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users")
class UserLocationController(
    private val userLocationService: UserLocationService
) {
    @PostMapping("/location")
    fun saveLocation(
        @RequestBody request: LocationSaveRequest,
        @RequestHeader("X-USER-ID") userId: Long
    ): ApiResponse<String> {
        userLocationService.upsertUserLocation(userId, request)
        return ApiResponse.success("위치 저장 또는 업데이트 완료")
    }
}
