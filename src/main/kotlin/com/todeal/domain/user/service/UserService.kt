package com.todeal.domain.user.service
import com.todeal.domain.auth.JwtProvider
import com.todeal.domain.user.dto.*
import com.todeal.domain.user.entity.UserAgreementEntity
import com.todeal.domain.user.entity.UserEntity
import com.todeal.domain.user.repository.UserAgreementRepository
import com.todeal.domain.user.repository.UserRepository
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val jwtProvider: JwtProvider,
    private val userRepository: UserRepository,
    private val userAgreementRepository: UserAgreementRepository,
    private val passwordEncoder: BCryptPasswordEncoder
) {

    @Transactional
    fun signup(request: UserSignupRequest): UserResponse {
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

        return UserResponse.from(savedUser)
    }

    fun login(request: UserLoginRequest): LoginResponse {
        val user = userRepository.findByEmail(request.email)
            ?: throw IllegalArgumentException("이메일 또는 비밀번호가 잘못되었습니다.")

        if (!passwordEncoder.matches(request.password, user.password)) {
            throw IllegalArgumentException("이메일 또는 비밀번호가 잘못되었습니다.")
        }

        val accessToken = jwtProvider.generateAccessToken(user.id)
        val refreshToken = jwtProvider.generateRefreshToken(user.id)

        return LoginResponse(accessToken, refreshToken, UserResponse.from(user))
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

    // 🔄 리네이밍: 테스트용 또는 임시 계정 생성용
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
}
