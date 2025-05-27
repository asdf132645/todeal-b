package com.todeal.domain.board.repository

import com.todeal.domain.board.entity.BoardPostEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface BoardPostRepository : JpaRepository<BoardPostEntity, Long> {

    // ✅ 위치 기반 - 거리만
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

    // ✅ 위치 + 카테고리
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

    // ✅ 위치 + 키워드 + 필드
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
          AND (
            (:field = 'title' AND LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%'))) OR
            (:field = 'content' AND LOWER(p.content) LIKE LOWER(CONCAT('%', :keyword, '%'))) OR
            (:field = 'nickname' AND LOWER(p.nickname) LIKE LOWER(CONCAT('%', :keyword, '%')))
          )
        ORDER BY p.createdAt DESC
        """
    )
    fun findWithinDistanceAndKeyword(
        @Param("latitude") latitude: Double,
        @Param("longitude") longitude: Double,
        @Param("latMin") latMin: Double,
        @Param("latMax") latMax: Double,
        @Param("lngMin") lngMin: Double,
        @Param("lngMax") lngMax: Double,
        @Param("distance") distance: Double,
        @Param("keyword") keyword: String,
        @Param("field") field: String
    ): List<BoardPostEntity>

    // ✅ 위치 + 카테고리 + 키워드 + 필드
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
          AND (
            (:field = 'title' AND LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%'))) OR
            (:field = 'content' AND LOWER(p.content) LIKE LOWER(CONCAT('%', :keyword, '%'))) OR
            (:field = 'nickname' AND LOWER(p.nickname) LIKE LOWER(CONCAT('%', :keyword, '%')))
          )
        ORDER BY p.createdAt DESC
        """
    )
    fun findWithinDistanceCategoryAndKeyword(
        @Param("latitude") latitude: Double,
        @Param("longitude") longitude: Double,
        @Param("latMin") latMin: Double,
        @Param("latMax") latMax: Double,
        @Param("lngMin") lngMin: Double,
        @Param("lngMax") lngMax: Double,
        @Param("distance") distance: Double,
        @Param("category") category: String,
        @Param("keyword") keyword: String,
        @Param("field") field: String
    ): List<BoardPostEntity>

    // ✅ 비위치: 카테고리 + 키워드 + 필드
    @Query(
        """
        SELECT p FROM BoardPostEntity p
        WHERE p.category = :category AND (
            (:field = 'title' AND LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%'))) OR
            (:field = 'content' AND LOWER(p.content) LIKE LOWER(CONCAT('%', :keyword, '%'))) OR
            (:field = 'nickname' AND LOWER(p.nickname) LIKE LOWER(CONCAT('%', :keyword, '%')))
        )
        ORDER BY p.createdAt DESC
        """
    )
    fun findByCategoryAndKeyword(
        @Param("category") category: String,
        @Param("keyword") keyword: String,
        @Param("field") field: String
    ): List<BoardPostEntity>

    // ✅ 비위치: 키워드 + 필드만
    @Query(
        """
        SELECT p FROM BoardPostEntity p
        WHERE (
            (:field = 'title' AND LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%'))) OR
            (:field = 'content' AND LOWER(p.content) LIKE LOWER(CONCAT('%', :keyword, '%'))) OR
            (:field = 'nickname' AND LOWER(p.nickname) LIKE LOWER(CONCAT('%', :keyword, '%')))
        )
        ORDER BY p.createdAt DESC
        """
    )
    fun findByKeyword(
        @Param("field") field: String,
        @Param("keyword") keyword: String
    ): List<BoardPostEntity>

    fun findByCategoryOrderByCreatedAtDesc(category: String): List<BoardPostEntity>
    fun findTop100ByOrderByCreatedAtDesc(): List<BoardPostEntity>
    fun findByUserId(userId: Long): List<BoardPostEntity>
}
