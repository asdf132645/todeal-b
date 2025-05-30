package com.todeal.domain.user.controller

import com.todeal.domain.user.dto.*
import com.todeal.domain.user.service.UserService
import com.todeal.global.response.ApiResponse
import com.todeal.infrastructure.redis.RedisFcmTokenService
import org.springframework.web.bind.annotation.*
import com.todeal.domain.auth.JwtProvider
import jakarta.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/users")
class UserController(
    private val userService: UserService,
    private val redisFcmTokenService: RedisFcmTokenService,
    private val jwtProvider: JwtProvider
) {

    @PostMapping("/signup")
    fun signup(@RequestBody request: UserSignupRequest): ApiResponse<LoginResponse> {
        val result = userService.signup(request)
        return ApiResponse.success(result)
    }

    @PostMapping("/login")
    fun login(
        servletRequest: HttpServletRequest,
        @RequestBody body: UserLoginRequest
    ): ApiResponse<LoginResponse> {
        val ip = servletRequest.remoteAddr
        val loginRequest = body.copy(ip = ip) // ✅ device는 body에서 받은 값 그대로 유지
        val result = userService.login(loginRequest)
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

        redisFcmTokenService.addToken(userId, token)
        return ApiResponse.success("토큰 등록 완료")
    }

    @DeleteMapping("/me/fcm-token")
    fun deleteFcmToken(
        @RequestHeader("X-USER-ID") userId: Long
    ): ApiResponse<String> {
        redisFcmTokenService.deleteAllTokens(userId)
        return ApiResponse.success("토큰 삭제 완료")
    }

    @GetMapping("/me")
    fun getMyInfo(@RequestHeader("Authorization") bearer: String): ApiResponse<UserResponse> {
        val token = bearer.removePrefix("Bearer ").trim()
        val userId = jwtProvider.getUserIdFromToken(token)
        val result = userService.getById(userId)
        return ApiResponse.success(result)
    }

    @GetMapping("/check-email")
    fun checkEmail(@RequestParam email: String): ApiResponse<Map<String, Boolean>> {
        val exists = userService.existsByEmail(email)
        return ApiResponse.success(mapOf("exists" to exists))
    }

    @GetMapping("/check-nickname")
    fun checkNickname(@RequestParam nickname: String): ApiResponse<Map<String, Boolean>> {
        val exists = userService.existsByNickname(nickname)
        return ApiResponse.success(mapOf("exists" to exists))
    }

}
