package com.todeal.domain.user.infra

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
class EmailCodeStore(
    private val redisTemplate: StringRedisTemplate
) {
    private fun key(email: String): String = "email:verify:$email"

    fun saveCode(email: String, code: String) {
        redisTemplate.opsForValue().set(key(email), code, 5, TimeUnit.MINUTES)
    }

    fun getCode(email: String): String? {
        return redisTemplate.opsForValue().get(key(email))
    }

    fun removeCode(email: String) {
        redisTemplate.delete(key(email))
    }
}
