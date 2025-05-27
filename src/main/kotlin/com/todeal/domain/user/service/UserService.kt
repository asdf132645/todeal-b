package com.todeal.domain.user.service

import com.todeal.domain.auth.JwtProvider
import com.todeal.domain.auth.entity.RefreshTokenEntity
import com.todeal.domain.auth.repository.RefreshTokenRepository
import com.todeal.domain.log.entity.LoginLogEntity
import com.todeal.domain.log.repository.LoginLogRepository
import com.todeal.domain.user.dto.*
import com.todeal.domain.user.entity.UserAgreementEntity
import com.todeal.domain.user.entity.UserEntity
import com.todeal.domain.user.repository.UserAgreementRepository
import com.todeal.domain.user.repository.UserRepository
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class UserService(
    private val jwtProvider: JwtProvider,
    private val userRepository: UserRepository,
    private val userAgreementRepository: UserAgreementRepository,
    private val passwordEncoder: BCryptPasswordEncoder,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val loginLogRepository: LoginLogRepository
) {

    @Transactional
    fun signup(request: UserSignupRequest): LoginResponse {
        if (request.email == null || request.password == null) {
            throw IllegalArgumentException("이메일과 비밀번호는 필수입니다.")
        }

        if (userRepository.existsByEmail(request.email)) {
            throw IllegalArgumentException("이미 존재하는 이메일입니다")
        }

        val user = UserEntity(
            email = request.email,
            password = passwordEncoder.encode(request.password),
            nickname = request.nickname,
            phone = request.phone,
            profileImageUrl = request.profileImageUrl,
            role = "USER",
            isPremium = false,
            planExpireAt = null,
            kakaoId = null,
            locationAgree = request.locationAgree,
            latitude = request.latitude,
            longitude = request.longitude
        )

        val savedUser = userRepository.save(user)

        val agreements = request.agreements.map { type ->
            UserAgreementEntity(user = savedUser, type = type)
        }
        userAgreementRepository.saveAll(agreements)

        val accessToken = jwtProvider.generateAccessToken(savedUser.id)
        val refreshToken = jwtProvider.generateRefreshToken()

        saveOrUpdateRefreshToken(savedUser.id, refreshToken, "EMAIL")

        return LoginResponse(
            accessToken = accessToken,
            refreshToken = refreshToken,
            user = LoginUserDto.from(savedUser)
        )
    }

    fun login(request: UserLoginRequest): LoginResponse {
        val user = userRepository.findByEmail(request.email)
            ?: throw IllegalArgumentException("이메일 또는 비밀번호가 잘못되었습니다.")

        if (!passwordEncoder.matches(request.password, user.password)) {
            throw IllegalArgumentException("이메일 또는 비밀번호가 잘못되었습니다.")
        }

        if (user.isBanned) {
            throw IllegalStateException("계정이 정지되었습니다. 투딜 관리자에게 문의를 넣어주세요. 사유: ${user.banReason ?: "신고 누적"}")
        }

        val accessToken = jwtProvider.generateAccessToken(user.id)
        val refreshToken = jwtProvider.generateRefreshToken()

        saveOrUpdateRefreshToken(user.id, refreshToken, "EMAIL")

        loginLogRepository.save(
            LoginLogEntity(
                userId = user.id,
                ipAddress = request.ip,
                deviceInfo = request.device
            )
        )

        return LoginResponse(
            accessToken = accessToken,
            refreshToken = refreshToken,
            user = LoginUserDto.from(user)
        )
    }

    fun update(id: Long, request: UserUpdateRequest): UserResponse {
        val user = userRepository.findById(id)
            .orElseThrow { NoSuchElementException("사용자를 찾을 수 없습니다.") }

        val updated = user.copy(
            nickname = request.nickname ?: user.nickname,
            profileImageUrl = request.profileImageUrl ?: user.profileImageUrl
        )

        return UserResponse.from(userRepository.save(updated))
    }

    fun getById(id: Long): UserResponse {
        return userRepository.findById(id)
            .map { UserResponse.from(it) }
            .orElseThrow { NoSuchElementException("사용자를 찾을 수 없습니다.") }
    }

    fun signupBasic(email: String, password: String, nickname: String, phone: String?): UserDto {
        if (userRepository.existsByEmail(email)) {
            throw IllegalArgumentException("이미 존재하는 이메일입니다")
        }

        val user = UserEntity(
            email = email,
            password = passwordEncoder.encode(password),
            nickname = nickname,
            phone = phone,
            kakaoId = null,
            role = "USER",
            isPremium = false,
            planExpireAt = null,
            locationAgree = false
        )

        return userRepository.save(user).toDto()
    }

    fun loginBasic(email: String, password: String): UserDto {
        val user = userRepository.findByEmail(email)
            ?: throw IllegalArgumentException("존재하지 않는 사용자입니다")

        if (!passwordEncoder.matches(password, user.password)) {
            throw IllegalArgumentException("비밀번호가 일치하지 않습니다")
        }

        return user.toDto()
    }

    fun findById(id: Long): UserDto {
        return userRepository.findById(id).orElseThrow().toDto()
    }

    fun existsByEmail(email: String): Boolean {
        return userRepository.existsByEmail(email)
    }

    fun existsByNickname(nickname: String): Boolean {
        return userRepository.existsByNickname(nickname)
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
