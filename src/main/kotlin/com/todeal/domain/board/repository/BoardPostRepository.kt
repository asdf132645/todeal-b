// ✅ BoardPostRepository.kt
package com.todeal.domain.board.repository

import com.todeal.domain.board.entity.BoardPostEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface BoardPostRepository : JpaRepository<BoardPostEntity, Long> {

    @Query(
        """
        SELECT p FROM BoardPostEntity p
        WHERE (6371 * acos(
            cos(radians(:latitude)) * cos(radians(p.latitude)) *
            cos(radians(p.longitude) - radians(:longitude)) +
            sin(radians(:latitude)) * sin(radians(p.latitude))
        )) <= :distance
        ORDER BY p.createdAt DESC
        """
    )
    fun findWithinDistance(
        @Param("latitude") latitude: Double,
        @Param("longitude") longitude: Double,
        @Param("distance") distance: Double
    ): List<BoardPostEntity>

    fun findTop100ByOrderByCreatedAtDesc(): List<BoardPostEntity>
    fun findByUserId(userId: Long): List<BoardPostEntity>
}
