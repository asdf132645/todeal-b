package com.todeal.domain.trustscore.dto

import com.todeal.domain.trustscore.model.TrustScoreType
import java.time.LocalDateTime

data class TrustScoreResponse(
    val fromUserId: Long,
    val dealId: Long,
    val type: TrustScoreType,
    val isPositive: Boolean,
    val comment: String?,
    val createdAt: LocalDateTime
)
