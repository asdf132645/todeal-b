package com.todeal.domain.hashtag.repository

import com.todeal.domain.hashtag.entity.DealHashtagEntity
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.domain.Pageable

interface DealHashtagRepository : CrudRepository<DealHashtagEntity, Long> {

    @Query(
        """
        SELECT h.name
        FROM HashtagEntity h
        JOIN DealHashtagEntity dh ON h.id = dh.hashtagId
        GROUP BY h.name
        ORDER BY COUNT(dh.id) DESC
        """
    )
    fun findPopularHashtags(pageable: Pageable): List<String>
}
