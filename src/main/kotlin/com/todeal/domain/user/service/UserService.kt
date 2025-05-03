package com.todeal.domain.user.service

import com.todeal.domain.user.dto.*
import com.todeal.domain.user.entity.UserEntity
import com.todeal.domain.user.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository
) {

    fun signup(request: UserSignupRequest): UserResponse {
        val user = UserEntity(
            email = request.email,
            password = request.password, // TODO: 비밀번호 암호화 필요
            nickname = request.nickname,
            profileImageUrl = request.profileImageUrl,
            role = "USER",
            isPremium = false,
            planExpireAt = null
        )
        return UserResponse.from(userRepository.save(user))
    }

    fun login(request: UserLoginRequest): UserResponse {
        val user = userRepository.findByEmail(request.email)
            .orElseThrow { IllegalArgumentException("이메일 또는 비밀번호가 잘못되었습니다.") }

        if (user.password != request.password) {
            throw IllegalArgumentException("이메일 또는 비밀번호가 잘못되었습니다.")
        }

        return UserResponse.from(user)
    }

    fun update(id: Long, request: UserUpdateRequest): UserResponse {
        val user = userRepository.findById(id).orElseThrow { NoSuchElementException("사용자를 찾을 수 없습니다.") }

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
}
