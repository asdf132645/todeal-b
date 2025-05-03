// UserRepository.kt
package com.todeal.domain.user.repository

import com.todeal.domain.user.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface UserRepository : JpaRepository<UserEntity, Long> {
    fun findByEmail(email: String): Optional<UserEntity>
}
