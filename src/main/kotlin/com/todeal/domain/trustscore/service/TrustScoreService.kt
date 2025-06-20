package com.todeal.domain.trustscore.service

import com.todeal.domain.deal.repository.DealRepository
import com.todeal.domain.trustscore.dto.TrustScoreResponse
import com.todeal.domain.trustscore.entity.TrustScoreEntity
import com.todeal.domain.trustscore.model.TrustScoreType
import com.todeal.domain.trustscore.repository.TrustScoreRepository
import com.todeal.domain.user.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

@Service
class TrustScoreService(
    private val trustScoreRepository: TrustScoreRepository,
    private val userRepository: UserRepository,
    private val dealRepository: DealRepository
) {

    fun getReviewsForUser(userId: Long, type: TrustScoreType?, pageable: Pageable): Page<TrustScoreResponse> {
        val page = if (type != null) {
            trustScoreRepository.findAllByToUserIdAndType(userId, type, pageable)
        } else {
            trustScoreRepository.findAllByToUserId(userId, pageable)
        }

        return page.map {
            TrustScoreResponse(
                fromUserId = it.fromUserId,
                dealId = it.dealId,
                type = it.type,
                isPositive = it.isPositive,
                comment = it.comment,
                createdAt = it.createdAt
            )
        }
    }


    fun getUserScores(userIds: List<Long>): Map<Long, Double> {
        val stats = trustScoreRepository.fetchScoreStatsByToUserIds(userIds)

        val result = mutableMapOf<Long, Double>()
        stats.forEach {
            val base = 50.0
            val score = base + it.getPositiveCount() - (it.getTotalCount() - it.getPositiveCount())
            result[it.getUserId()] = score.coerceIn(0.0, 100.0)
        }

        // 평가 없으면 기본값 50점
        userIds.forEach {
            if (!result.containsKey(it)) result[it] = 50.0
        }

        return result
    }


    @Transactional
    fun submitScore(fromUserId: Long, toUserId: Long, dealId: Long, isPositive: Boolean, comment: String?) {
        if (fromUserId == toUserId) {
            throw IllegalArgumentException("자기 자신을 평가할 수 없습니다.")
        }

        if (trustScoreRepository.existsByFromUserIdAndToUserIdAndDealId(fromUserId, toUserId, dealId)) {
            throw IllegalStateException("이미 이 사용자에게 해당 딜에서 평가를 완료했습니다.")
        }

        val deal = dealRepository.findById(dealId)
            .orElseThrow { IllegalArgumentException("해당 딜이 존재하지 않습니다.") }

        val trustScoreType = when (deal.type.lowercase()) {
            "used" -> TrustScoreType.USED
            "parttime" -> TrustScoreType.PARTTIME
            "parttime-request" -> TrustScoreType.PARTTIME_REQUEST
            "barter" -> TrustScoreType.BARTER
            else -> throw IllegalArgumentException("지원하지 않는 딜 타입입니다: ${deal.type}")
        }

        val scoreEntity = TrustScoreEntity(
            fromUserId = fromUserId,
            toUserId = toUserId,
            dealId = dealId,
            type = trustScoreType,
            isPositive = isPositive,
            comment = comment
        )
        trustScoreRepository.save(scoreEntity)

        val targetUser = userRepository.findById(toUserId)
            .orElseThrow { IllegalArgumentException("해당 유저가 존재하지 않습니다.") }

        val updatedScore = if (isPositive) {
            (targetUser.trustScore + 1.0).coerceAtMost(100.0)
        } else {
            (targetUser.trustScore - 1.0).coerceAtLeast(0.0)
        }

        targetUser.trustScore = updatedScore
        userRepository.save(targetUser)
    }
}
