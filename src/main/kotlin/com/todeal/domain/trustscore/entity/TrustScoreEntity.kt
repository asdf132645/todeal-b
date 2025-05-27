// ✅ TrustScoreEntity.kt
package com.todeal.domain.trustscore.entity

import com.todeal.domain.trustscore.model.TrustScoreType
import jakarta.persistence.*
import java.time.LocalDateTime


@Entity
@Table(name = "trust_score")
data class TrustScoreEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val fromUserId: Long,

    @Column(nullable = false)
    val toUserId: Long,

    @Column(nullable = false)
    val dealId: Long,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val type: TrustScoreType,

    @Column(nullable = false)
    val isPositive: Boolean,

    @Column(columnDefinition = "TEXT")
    val comment: String? = null,

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)
