package com.todeal.global.redis

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class RedisCacheService(
    private val redisTemplate: StringRedisTemplate
) {

    fun isFirstView(viewKey: String, ttlSeconds: Long): Boolean {
        val ops = redisTemplate.opsForValue()
        val existed = ops.get(viewKey)

        return if (existed == null) {
            ops.set(viewKey, "1", Duration.ofSeconds(ttlSeconds))
            true
        } else {
            false
        }
    }
}
