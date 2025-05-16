// ✅ TrustScoreController.kt
package com.todeal.domain.trustscore.controller

import com.todeal.domain.trustscore.dto.TrustScoreRequest
import com.todeal.domain.trustscore.service.TrustScoreService
import com.todeal.global.response.ApiResponse
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/trust-scores")
class TrustScoreController(
    private val trustScoreService: TrustScoreService
) {

    @GetMapping
    fun getUserScores(
        @RequestParam userIds: List<Long>
    ): ApiResponse<Map<Long, Double>> {
        val scores = trustScoreService.getUserScores(userIds)
        return ApiResponse.success(scores)
    }

    @PostMapping("/submit")
    fun submitTrustScore(
        @RequestBody request: TrustScoreRequest,
        @RequestHeader("X-USER-ID") fromUserId: Long
    ): ApiResponse<String> {
        trustScoreService.submitScore(fromUserId, request.toUserId, request.dealId, request.isPositive)
        return ApiResponse.success("평가 완료")
    }
}
