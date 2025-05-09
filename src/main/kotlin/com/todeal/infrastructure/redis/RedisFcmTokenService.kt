package com.todeal.infrastructure.redis

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class RedisFcmTokenService(
    private val redisTemplate: RedisTemplate<String, String>
) {
    private fun key(userId: Long) = "user:$userId:fcmToken"

    fun saveToken(userId: Long, token: String) {
        redisTemplate.opsForValue().set(key(userId), token, 30, TimeUnit.DAYS)
    }

    fun getToken(userId: Long): String? {
        return redisTemplate.opsForValue().get(key(userId))
    }
    fun deleteToken(userId: Long) {
        redisTemplate.delete("user:$userId:fcmToken")
    }
}
