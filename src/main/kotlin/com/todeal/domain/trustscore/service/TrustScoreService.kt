// ✅ TrustScoreService.kt
package com.todeal.domain.trustscore.service

import com.todeal.domain.trustscore.entity.TrustScoreEntity
import com.todeal.domain.trustscore.repository.TrustScoreRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TrustScoreService(
    private val trustScoreRepository: TrustScoreRepository
) {

    fun getUserScores(userIds: List<Long>): Map<Long, Double> {
        val stats = trustScoreRepository.getScoreStatsForUsers(userIds)

        val result = mutableMapOf<Long, Double>()
        stats.forEach {
            val score = if (it.getTotalCount() > 0) {
                (it.getPositiveCount().toDouble() / it.getTotalCount()) * 100.0
            } else {
                50.0  // ✅ 기본값 50점
            }
            result[it.getUserId()] = String.format("%.1f", score).toDouble()
        }

        // 점수 없는 유저는 기본값 50.0으로 설정
        userIds.forEach { if (!result.containsKey(it)) result[it] = 50.0 }

        return result
    }


    @Transactional
    fun submitScore(fromUserId: Long, toUserId: Long, dealId: Long, isPositive: Boolean) {
        // ❌ 자기 자신에게 평가 불가
        if (fromUserId == toUserId) {
            throw IllegalArgumentException("자기 자신을 평가할 수 없습니다.")
        }

        // ❌ 중복 평가 방지
        val alreadyExists = trustScoreRepository.existsByFromUserIdAndToUserIdAndDealId(
            fromUserId,
            toUserId,
            dealId
        )
        if (alreadyExists) {
            throw IllegalStateException("이미 이 사용자에게 해당 딜에서 평가를 완료했습니다.")
        }

        // ✅ 저장
        val scoreEntity = TrustScoreEntity(
            fromUserId = fromUserId,
            toUserId = toUserId,
            dealId = dealId,
            isPositive = isPositive
        )

        trustScoreRepository.save(scoreEntity)
    }
}
