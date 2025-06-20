package com.todeal.domain.analytics.repository

import com.todeal.domain.analytics.entity.SearchLogEntity
import io.lettuce.core.dynamic.annotation.Param
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface SearchLogRepository : JpaRepository<SearchLogEntity, Long> {

    fun findTop10ByCreatedAtAfterOrderByCreatedAtDesc(date: LocalDateTime): List<SearchLogEntity>

    @Query(
        """
        SELECT keyword, COUNT(*) AS count
        FROM search_logs
        WHERE created_at > :since
        GROUP BY keyword
        ORDER BY count DESC
        LIMIT :limit
        """,
        nativeQuery = true
    )
    fun findTopKeywords(@Param("since") since: LocalDateTime, @Param("limit") limit: Int): List<Map<String, Any>>
}
