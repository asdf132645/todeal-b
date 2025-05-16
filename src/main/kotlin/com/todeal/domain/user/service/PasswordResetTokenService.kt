package com.todeal.domain.user.service

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class PasswordResetTokenService(
    private val redisTemplate: StringRedisTemplate
) {
    private val TTL = Duration.ofMinutes(30)

    fun saveToken(token: String, userId: Long) {
        redisTemplate.opsForValue().set("reset:$token", userId.toString(), TTL)
    }

    fun getUserIdByToken(token: String): Long? {
        return redisTemplate.opsForValue().get("reset:$token")?.toLongOrNull()
    }

    fun deleteToken(token: String) {
        redisTemplate.delete("reset:$token")
    }
}
