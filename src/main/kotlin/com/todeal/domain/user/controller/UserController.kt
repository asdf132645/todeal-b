package com.todeal.domain.user.controller

import com.todeal.domain.user.dto.*
import com.todeal.domain.user.service.UserService
import com.todeal.global.response.ApiResponse
import com.todeal.infrastructure.redis.RedisFcmTokenService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService,
    private val redisFcmTokenService: RedisFcmTokenService // ✅ Redis 주입 추가
) {

    @PostMapping("/signup")
    fun signup(@RequestBody request: UserSignupRequest): ApiResponse<UserResponse> {
        val result = userService.signup(request)
        return ApiResponse.success(result)
    }

    @PostMapping("/login")
    fun login(@RequestBody request: UserLoginRequest): ApiResponse<UserResponse> {
        val result = userService.login(request)
        return ApiResponse.success(result)
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody request: UserUpdateRequest): ApiResponse<UserResponse> {
        val result = userService.update(id, request)
        return ApiResponse.success(result)
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): ApiResponse<UserResponse> {
        val result = userService.getById(id)
        return ApiResponse.success(result)
    }

    // ✅ FCM 토큰 등록 API
    @PatchMapping("/me/fcm-token")
    fun updateFcmToken(
        @RequestHeader("X-USER-ID") userId: Long,
        @RequestBody body: Map<String, String>
    ): ApiResponse<out String> {
        val token = body["fcmToken"]
        if (token.isNullOrBlank()) {
            return ApiResponse.fail("토큰 누락") // ✅ 반드시 <String> 명시
        }

        redisFcmTokenService.saveToken(userId, token)
        return ApiResponse.success("토큰 등록 완료")
    }


    @DeleteMapping("/me/fcm-token")
    fun deleteFcmToken(
        @RequestHeader("X-USER-ID") userId: Long
    ): ApiResponse<String> {
        redisFcmTokenService.deleteToken(userId)
        return ApiResponse.success("토큰 삭제 완료")
    }

}
