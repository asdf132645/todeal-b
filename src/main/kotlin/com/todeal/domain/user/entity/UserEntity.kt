package com.todeal.domain.user.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "users")
data class UserEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = true, unique = true)
    val kakaoId: Long? = null,  // 카카오 전용

    @Column(nullable = true, unique = true)
    val email: String? = null,

    @Column(nullable = true)
    var password: String? = null,  // 일반 로그인 전용 (BCrypt 해시 저장)

    @Column(nullable = false)
    val nickname: String?,

    val phone: String? = null,
    val profileImageUrl: String? = null,

    @Column(nullable = false)
    val role: String = "USER",

    @Column(nullable = false)
    val isPremium: Boolean = false,

    val planExpireAt: LocalDateTime? = null,

    @Column(nullable = false)
    var isBanned: Boolean = false,

    @Column(name = "report_count", nullable = false)
    var reportCount: Int = 0,

    @Column
    var banReason: String? = null,

    @Column(nullable = false)
    val locationAgree: Boolean = false,

    val latitude: Double? = null,
    val longitude: Double? = null,

    @Column(nullable = false)
    var trustScore: Double = 50.0,

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    val agreements: MutableList<UserAgreementEntity> = mutableListOf()
)
