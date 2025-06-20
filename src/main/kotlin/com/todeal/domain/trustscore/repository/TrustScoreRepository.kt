package com.todeal.domain.trustscore.repository

import com.todeal.domain.trustscore.entity.TrustScoreEntity
import com.todeal.domain.trustscore.model.TrustScoreType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface TrustScoreRepository : JpaRepository<TrustScoreEntity, Long> {

    // ✅ 특정 유저가 특정 유저를 특정 딜에서 평가했는지 여부
    fun existsByFromUserIdAndToUserIdAndDealId(
        fromUserId: Long,
        toUserId: Long,
        dealId: Long
    ): Boolean

    // ✅ 특정 유저가 여러 딜에 대해 평가한 기록 가져오기
    @Query("""
        SELECT t FROM TrustScoreEntity t
        WHERE t.fromUserId = :fromUserId AND t.dealId IN :dealIds
    """)
    fun findByFromUserIdAndDealId(
        @Param("fromUserId") fromUserId: Long,
        @Param("dealIds") dealIds: List<Long>
    ): List<TrustScoreEntity>

    // ✅ 여러 사용자들의 평판 통계 (긍정 개수, 전체 개수)
    @Query("""
        SELECT ts.toUserId AS userId,
               SUM(CASE WHEN ts.isPositive = true THEN 1 ELSE 0 END) AS positiveCount,
               COUNT(ts.id) AS totalCount
        FROM TrustScoreEntity ts
        WHERE ts.toUserId IN :userIds
        GROUP BY ts.toUserId
    """)
    fun getScoreStatsForUsers(
        @Param("userIds") userIds: List<Long>
    ): List<TrustScoreProjection>

    // ✅ 특정 사용자의 전체 평판 리스트
    @Query("SELECT ts FROM TrustScoreEntity ts WHERE ts.toUserId = :userId")
    fun findAllByToUserId(
        @Param("userId") userId: Long,
        pageable: Pageable
    ): Page<TrustScoreEntity>

    // ✅ 특정 사용자 + 평판 타입 조건으로 평판 리스트
    @Query("SELECT ts FROM TrustScoreEntity ts WHERE ts.toUserId = :userId AND ts.type = :type")
    fun findAllByToUserIdAndType(
        @Param("userId") userId: Long,
        @Param("type") type: TrustScoreType,
        pageable: Pageable
    ): Page<TrustScoreEntity>
    @Query("""
    SELECT ts.toUserId AS userId,
           SUM(CASE WHEN ts.isPositive = true THEN 1 ELSE 0 END) AS positiveCount,
           COUNT(ts.id) AS totalCount
    FROM TrustScoreEntity ts
    WHERE ts.toUserId IN :userIds
    GROUP BY ts.toUserId
""")
    fun fetchScoreStatsByToUserIds(@Param("userIds") userIds: List<Long>): List<TrustScoreProjection>
}



interface TrustScoreProjection {
    fun getUserId(): Long
    fun getPositiveCount(): Long
    fun getTotalCount(): Long
}
