package com.todeal.domain.report.repository

import com.todeal.domain.report.entity.ReportEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface ReportRepository : JpaRepository<ReportEntity, Long> {
    fun existsByFromUserIdAndToUserIdAndDealId(fromUserId: Long, toUserId: Long, dealId: Long?): Boolean
    @Query("""
    SELECT toUserId 
    FROM ReportEntity
    GROUP BY toUserId
    HAVING COUNT(id) >= :threshold
""")
    fun findUserIdsWithReportCountOver(threshold: Long): List<Long>
}
