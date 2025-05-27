package com.todeal.domain.trustscore.controller

import com.todeal.domain.trustscore.dto.TrustScoreRequest
import com.todeal.domain.trustscore.dto.TrustScoreResponse
import com.todeal.domain.trustscore.model.TrustScoreType
import com.todeal.domain.trustscore.service.TrustScoreService
import com.todeal.global.response.ApiResponse
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.Sort

@RestController
@RequestMapping("/trust-scores")
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
        trustScoreService.submitScore(
            fromUserId = fromUserId,
            toUserId = request.toUserId,
            dealId = request.dealId,
            isPositive = request.isPositive,
            comment = request.comment
        )
        return ApiResponse.success("평가 완료")
    }

    @GetMapping("/user/{userId}/reviews")
    fun getUserReviews(
        @PathVariable userId: Long,
        @RequestParam(required = false) type: TrustScoreType?, // optional 필터
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ApiResponse<Page<TrustScoreResponse>> {
        val pageable: Pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))
        val reviews = trustScoreService.getReviewsForUser(userId, type, pageable)
        return ApiResponse.success(reviews)
    }

}
