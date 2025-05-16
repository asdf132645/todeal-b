// ✅ TrustScoreRequest.kt
package com.todeal.domain.trustscore.dto

data class TrustScoreRequest(
    val toUserId: Long,
    val dealId: Long,
    val isPositive: Boolean
)
