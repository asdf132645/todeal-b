package com.todeal.domain.auth.controller

import com.todeal.domain.auth.dto.*
import com.todeal.domain.auth.service.AuthService
import com.todeal.global.response.ApiResponse
import jakarta.servlet.http.HttpServletRequest
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
class AuthController(
    private val authService: AuthService
) {

    @PostMapping("/kakao-login")
    fun kakaoLogin(
        servletRequest: HttpServletRequest,
        @RequestBody request: KakaoLoginRequest
    ): ApiResponse<Any> {
        val ip = servletRequest.remoteAddr
        val device = request.device ?: "KAKAO"

        val result = authService.kakaoLogin(request.accessToken, ip, device)
        return ApiResponse.success(result)
    }

    @PostMapping("/signup")
    fun signupWithKakao(
        @RequestBody request: SignupRequest,
        @RequestHeader("Authorization") bearer: String
    ): ApiResponse<Map<String, String>> {
        val token = bearer.removePrefix("Bearer ").trim()

        // ✅ accessToken + refreshToken 함께 반환하도록 변경
        val (accessToken, refreshToken) = authService.signupWithKakao(token, request)

        return ApiResponse.success(
            mapOf(
                "accessToken" to accessToken,
                "refreshToken" to refreshToken
            )
        )
    }

    @PostMapping("/refresh-token")
    fun refreshToken(@RequestBody body: Map<String, String>): ApiResponse<Map<String, String>> {
        val token = body["refreshToken"] ?: throw IllegalArgumentException("리프레시 토큰 누락")

        // ✅ accessToken + 새 refreshToken 둘 다 재발급
        val (accessToken, newRefreshToken) = authService.refreshAccessToken(token)

        return ApiResponse.success(
            mapOf(
                "accessToken" to accessToken,
                "refreshToken" to newRefreshToken
            )
        )
    }
}
