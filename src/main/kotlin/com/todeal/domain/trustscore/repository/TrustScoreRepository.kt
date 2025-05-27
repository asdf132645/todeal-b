// ✅ TrustScoreRepository.kt
package com.todeal.domain.trustscore.repository

import com.todeal.domain.trustscore.entity.TrustScoreEntity
import com.todeal.domain.trustscore.model.TrustScoreType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface TrustScoreRepository : JpaRepository<TrustScoreEntity, Long> {

    @Query(
        """
        SELECT ts.toUserId AS userId,
               SUM(CASE WHEN ts.isPositive = true THEN 1 ELSE 0 END) AS positiveCount,
               COUNT(ts.id) AS totalCount
        FROM TrustScoreEntity ts
        WHERE ts.toUserId IN :userIds
        GROUP BY ts.toUserId
        """
    )
    fun getScoreStatsForUsers(userIds: List<Long>): List<TrustScoreProjection>
    fun existsByFromUserIdAndToUserIdAndDealId(fromUserId: Long, toUserId: Long, dealId: Long): Boolean
    fun findByToUserIdAndType(userId: Long, type: TrustScoreType, pageable: Pageable): Page<TrustScoreEntity>
    fun findByToUserId(userId: Long, pageable: Pageable): Page<TrustScoreEntity>

}

interface TrustScoreProjection {
    fun getUserId(): Long
    fun getPositiveCount(): Long
    fun getTotalCount(): Long
}
