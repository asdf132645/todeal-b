// ✅ controller/AuthController.kt
package com.todeal.domain.auth.controller

import com.todeal.domain.auth.dto.*
import com.todeal.domain.auth.service.AuthService
import com.todeal.global.response.ApiResponse
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService
) {
    @PostMapping("/kakao-login")
    fun kakaoLogin(@RequestBody request: KakaoLoginRequest): ApiResponse<Any> {
        val result = authService.kakaoLogin(request.accessToken)
        return ApiResponse.success(result)
    }


    @PostMapping("/signup")
    fun signupWithKakao(
        @RequestBody request: SignupRequest,
        @RequestHeader("Authorization") bearer: String
    ): ApiResponse<Map<String, String>> {
        val token = bearer.removePrefix("Bearer ").trim()
        val accessToken = authService.signupWithKakao(token, request)
        return ApiResponse.success(mapOf("token" to accessToken))
    }

    @PostMapping("/refresh-token")
    fun refreshToken(@RequestBody body: Map<String, String>): ApiResponse<Map<String, String>> {
        val token = body["refreshToken"] ?: throw IllegalArgumentException("리프레시 토큰 누락")
        val newAccessToken = authService.refreshAccessToken(token)
        return ApiResponse.success(mapOf("accessToken" to newAccessToken))
    }
}
