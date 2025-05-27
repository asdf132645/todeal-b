package com.todeal.domain.auth.service

import com.todeal.domain.auth.JwtProvider
import com.todeal.domain.auth.dto.LoginResponse
import com.todeal.domain.auth.dto.SignupRequest
import com.todeal.domain.auth.entity.RefreshTokenEntity
import com.todeal.domain.auth.repository.RefreshTokenRepository
import com.todeal.domain.log.entity.LoginLogEntity
import com.todeal.domain.log.repository.LoginLogRepository
import com.todeal.domain.user.dto.UserResponse
import com.todeal.domain.user.entity.UserAgreementEntity
import com.todeal.domain.user.entity.UserEntity
import com.todeal.domain.user.repository.UserAgreementRepository
import com.todeal.domain.user.repository.UserRepository
import org.json.JSONObject
import org.springframework.stereotype.Service
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.LocalDateTime

@Service
class AuthService(
    private val jwtProvider: JwtProvider,
    private val userRepository: UserRepository,
    private val userAgreementRepository: UserAgreementRepository,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val loginLogRepository: LoginLogRepository
) {

    fun kakaoLogin(kakaoAccessToken: String, ip: String?, device: String?): Any {
        val kakaoId = fetchKakaoUserIdFromApi(kakaoAccessToken)
        val existing = userRepository.findByKakaoId(kakaoId)

        return if (existing != null) {
            val accessToken = jwtProvider.generateAccessToken(existing.id)
            val refreshToken = jwtProvider.generateRefreshToken()

            saveOrUpdateRefreshToken(existing.id, refreshToken, device ?: "KAKAO")

            loginLogRepository.save(
                LoginLogEntity(
                    userId = existing.id,
                    ipAddress = ip,
                    deviceInfo = device ?: "KAKAO"
                )
            )

            LoginResponse(accessToken, refreshToken, UserResponse.from(existing))
        } else {
            mapOf("isNewUser" to true, "tempToken" to kakaoAccessToken)
        }
    }

    fun signupWithKakao(kakaoAccessToken: String, req: SignupRequest): Pair<String, String> {
        val kakaoId = fetchKakaoUserIdFromApi(kakaoAccessToken)

        if (userRepository.existsByKakaoId(kakaoId)) {
            throw IllegalArgumentException("이미 가입된 카카오 계정입니다.")
        }

        val user = UserEntity(
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

        val savedUser = userRepository.save(user)

        val agreements = req.agreements.map { type ->
            UserAgreementEntity(user = savedUser, type = type)
        }
        userAgreementRepository.saveAll(agreements)

        val accessToken = jwtProvider.generateAccessToken(savedUser.id)
        val refreshToken = jwtProvider.generateRefreshToken()

        saveOrUpdateRefreshToken(savedUser.id, refreshToken, "KAKAO")

        return accessToken to refreshToken
    }

    fun refreshAccessToken(refreshToken: String): Pair<String, String> {
        val stored = refreshTokenRepository.findByToken(refreshToken)
            ?: throw IllegalArgumentException("리프레시 토큰이 유효하지 않습니다.")

        if (stored.expireAt.isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(stored)
            throw IllegalStateException("리프레시 토큰이 만료되었습니다.")
        }

        val newAccessToken = jwtProvider.generateAccessToken(stored.userId)
        val newRefreshToken = jwtProvider.generateRefreshToken()

        saveOrUpdateRefreshToken(stored.userId, newRefreshToken, stored.deviceInfo)

        return newAccessToken to newRefreshToken
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

    private fun saveOrUpdateRefreshToken(userId: Long, token: String, device: String?) {
        val safeDevice = device ?: "UNKNOWN"

        val existing = refreshTokenRepository.findByUserId(userId)
        if (existing != null) {
            val updated = existing.copy(
                token = token,
                expireAt = LocalDateTime.now().plusDays(14),
                deviceInfo = safeDevice
            )
            refreshTokenRepository.save(updated)
        } else {
            refreshTokenRepository.save(
                RefreshTokenEntity(
                    userId = userId,
                    token = token,
                    expireAt = LocalDateTime.now().plusDays(14),
                    deviceInfo = safeDevice
                )
            )
        }
    }

}
