package com.todeal.domain.trustscore.service

import com.todeal.domain.trustscore.entity.TrustScoreEntity
import com.todeal.domain.trustscore.repository.TrustScoreRepository
import com.todeal.domain.user.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TrustScoreService(
    private val trustScoreRepository: TrustScoreRepository,
    private val userRepository: UserRepository
) {

    fun getUserScores(userIds: List<Long>): Map<Long, Double> {
        val stats = trustScoreRepository.getScoreStatsForUsers(userIds)

        val result = mutableMapOf<Long, Double>()
        stats.forEach {
            val score = if (it.getTotalCount() > 0) {
                (it.getPositiveCount().toDouble() / it.getTotalCount()) * 100.0
            } else {
                50.0
            }
            result[it.getUserId()] = String.format("%.1f", score).toDouble()
        }

        userIds.forEach { if (!result.containsKey(it)) result[it] = 50.0 }

        return result
    }

    @Transactional
    fun submitScore(fromUserId: Long, toUserId: Long, dealId: Long, isPositive: Boolean) {
        if (fromUserId == toUserId) {
            throw IllegalArgumentException("자기 자신을 평가할 수 없습니다.")
        }

        val alreadyExists = trustScoreRepository.existsByFromUserIdAndToUserIdAndDealId(
            fromUserId, toUserId, dealId
        )
        if (alreadyExists) {
            throw IllegalStateException("이미 이 사용자에게 해당 딜에서 평가를 완료했습니다.")
        }

        // ✅ 신뢰도 이력 저장
        val scoreEntity = TrustScoreEntity(
            fromUserId = fromUserId,
            toUserId = toUserId,
            dealId = dealId,
            isPositive = isPositive
        )
        trustScoreRepository.save(scoreEntity)

        // ✅ 유저 신뢰도 업데이트
        val targetUser = userRepository.findById(toUserId)
            .orElseThrow { IllegalArgumentException("해당 유저가 존재하지 않습니다.") }

        val updatedScore = when (isPositive) {
            true -> (targetUser.trustScore + 1.0).coerceAtMost(100.0)
            false -> (targetUser.trustScore - 1.0).coerceAtLeast(0.0)
        }

        targetUser.trustScore = updatedScore
        userRepository.save(targetUser)
    }
}
