package com.todeal.domain.board.repository

import com.todeal.domain.board.entity.BoardPostEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface BoardPostRepository : JpaRepository<BoardPostEntity, Long> {

    // ✅ 사각형 + 거리 필터링 (더 정확하고 성능 좋음)
    @Query(
        """
        SELECT p FROM BoardPostEntity p
        WHERE p.latitude BETWEEN :latMin AND :latMax
          AND p.longitude BETWEEN :lngMin AND :lngMax
          AND (
            6371 * acos(
              cos(radians(:latitude)) * cos(radians(p.latitude)) *
              cos(radians(p.longitude) - radians(:longitude)) +
              sin(radians(:latitude)) * sin(radians(p.latitude))
            )
          ) <= :distance
        ORDER BY p.createdAt DESC
        """
    )
    fun findWithinDistance(
        @Param("latitude") latitude: Double,
        @Param("longitude") longitude: Double,
        @Param("latMin") latMin: Double,
        @Param("latMax") latMax: Double,
        @Param("lngMin") lngMin: Double,
        @Param("lngMax") lngMax: Double,
        @Param("distance") distance: Double
    ): List<BoardPostEntity>

    @Query(
        """
        SELECT p FROM BoardPostEntity p
        WHERE p.category = :category
          AND p.latitude BETWEEN :latMin AND :latMax
          AND p.longitude BETWEEN :lngMin AND :lngMax
          AND (
            6371 * acos(
              cos(radians(:latitude)) * cos(radians(p.latitude)) *
              cos(radians(p.longitude) - radians(:longitude)) +
              sin(radians(:latitude)) * sin(radians(p.latitude))
            )
          ) <= :distance
        ORDER BY p.createdAt DESC
        """
    )
    fun findWithinDistanceAndCategory(
        @Param("latitude") latitude: Double,
        @Param("longitude") longitude: Double,
        @Param("latMin") latMin: Double,
        @Param("latMax") latMax: Double,
        @Param("lngMin") lngMin: Double,
        @Param("lngMax") lngMax: Double,
        @Param("distance") distance: Double,
        @Param("category") category: String
    ): List<BoardPostEntity>

    fun findByCategoryOrderByCreatedAtDesc(category: String): List<BoardPostEntity>
    fun findTop100ByOrderByCreatedAtDesc(): List<BoardPostEntity>
    fun findByUserId(userId: Long): List<BoardPostEntity>
}
