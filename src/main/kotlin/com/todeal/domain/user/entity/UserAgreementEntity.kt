package com.todeal.domain.user.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "user_agreements")
data class UserAgreementEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: UserEntity,

    @Column(nullable = false)
    val type: String,  // 예: "terms", "marketing", "third_party"

    @Column(nullable = false)
    val agreedAt: LocalDateTime = LocalDateTime.now()
)
