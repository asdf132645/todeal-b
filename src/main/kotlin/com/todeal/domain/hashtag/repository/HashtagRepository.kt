package com.todeal.domain.hashtag.repository

import com.todeal.domain.hashtag.entity.HashtagEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.awt.print.Pageable

interface HashtagRepository : JpaRepository<HashtagEntity, Long> {
    fun findByName(name: String): HashtagEntity?

    @Query(
        """
    SELECT h.hashtag
    FROM deal_hashtags h
    JOIN deals d ON h.deal_id = d.id
    WHERE d.created_at >= NOW() - INTERVAL '7 days'
    GROUP BY h.hashtag
    ORDER BY COUNT(*) DESC
    LIMIT :limit
    """,
        nativeQuery = true
    )
    fun findWeeklyPopularHashtags(@Param("limit") limit: Int): List<String>
}
