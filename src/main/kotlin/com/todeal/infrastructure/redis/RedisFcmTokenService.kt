package com.todeal.infrastructure.redis

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class RedisFcmTokenService(
    private val redisTemplate: RedisTemplate<String, String>
) {
    private fun key(userId: Long) = "user:$userId:fcmTokens"

    // ✅ 다중 디바이스 지원: Set에 추가
    fun addToken(userId: Long, token: String) {
        val ops = redisTemplate.opsForSet()
        ops.add(key(userId), token)
        redisTemplate.expire(key(userId), 30, TimeUnit.DAYS) // TTL 적용
    }

    fun getAllTokens(userId: Long): Set<String> {
        return redisTemplate.opsForSet().members(key(userId)) ?: emptySet()
    }

    // ✅ 특정 디바이스 토큰 삭제
    fun removeToken(userId: Long, token: String) {
        redisTemplate.opsForSet().remove(key(userId), token)
    }

    // ✅ 전체 삭제 (로그아웃 등)
    fun deleteAllTokens(userId: Long) {
        redisTemplate.delete(key(userId))
    }
}
