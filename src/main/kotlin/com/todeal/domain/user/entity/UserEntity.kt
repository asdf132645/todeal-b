package com.todeal.domain.user.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "users")
data class UserEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, unique = true)
    val email: String,

    @Column(nullable = false)
    val nickname: String,

    @Column(nullable = false)
    val password: String,

    val profileImageUrl: String? = null,

    @Column(nullable = false)
    val role: String = "USER",

    @Column(nullable = false)
    val isPremium: Boolean = false,

    val planExpireAt: LocalDateTime? = null
)
