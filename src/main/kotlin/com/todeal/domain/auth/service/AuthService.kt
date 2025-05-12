package com.todeal.domain.auth.service

import com.todeal.domain.auth.JwtProvider
import com.todeal.domain.auth.dto.LoginResponse
import com.todeal.domain.auth.dto.SignupRequest
import com.todeal.domain.user.dto.UserResponse
import com.todeal.domain.user.entity.UserEntity
import com.todeal.domain.user.repository.UserRepository
import org.json.JSONObject
import org.springframework.stereotype.Service
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

@Service
class AuthService(
    private val jwtProvider: JwtProvider,
    private val userRepository: UserRepository
) {

    fun kakaoLogin(kakaoAccessToken: String): Any {
        val kakaoId = fetchKakaoUserIdFromApi(kakaoAccessToken)
        val existing = userRepository.findByKakaoId(kakaoId)

        return if (existing != null) {
            val accessToken = jwtProvider.generateAccessToken(existing.id)
            val refreshToken = jwtProvider.generateRefreshToken(existing.id)
            LoginResponse(accessToken, refreshToken, UserResponse.from(existing))
        } else {
            mapOf("isNewUser" to true, "tempToken" to kakaoAccessToken)
        }
    }

    fun signupWithKakao(kakaoAccessToken: String, req: SignupRequest): String {
        val kakaoId = fetchKakaoUserIdFromApi(kakaoAccessToken)

        val newUser = userRepository.save(
            UserEntity(
                kakaoId = kakaoId,
                email = req.email,
                password = null,
                nickname = req.nickname,
                phone = req.phone,
                role = "USER",
                isPremium = false,
                planExpireAt = null,
                profileImageUrl = null,
                locationAgree = req.locationAgree,
                latitude = req.latitude,
                longitude = req.longitude
            )
        )

        return jwtProvider.generateAccessToken(newUser.id)
    }

    private fun fetchKakaoUserIdFromApi(kakaoAccessToken: String): Long {
        val client = HttpClient.newHttpClient()
        val request = HttpRequest.newBuilder()
            .uri(URI.create("https://kapi.kakao.com/v2/user/me"))
            .header("Authorization", "Bearer $kakaoAccessToken")
            .GET()
            .build()

        val response = client.send(request, HttpResponse.BodyHandlers.ofString())

        if (response.statusCode() != 200) {
            throw IllegalArgumentException("카카오 사용자 정보를 가져오지 못했습니다. 응답 코드: ${response.statusCode()}")
        }

        val json = JSONObject(response.body())
        return json.getLong("id")
    }

    fun refreshAccessToken(refreshToken: String): String {
        if (refreshToken.isBlank() || refreshToken.count { it == '.' } != 2) {
            throw IllegalArgumentException("유효하지 않은 리프레시 토큰입니다.")
        }

        val userId = jwtProvider.getUserIdFromToken(refreshToken)
        return jwtProvider.generateAccessToken(userId)
    }
}
