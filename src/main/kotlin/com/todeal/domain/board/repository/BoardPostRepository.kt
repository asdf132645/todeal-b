package com.todeal.domain.board.repository

import com.todeal.domain.board.entity.BoardPostEntity
import org.springframework.data.domain.Page
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import org.springframework.data.domain.Pageable
import java.time.LocalDateTime

@Repository
interface BoardPostRepository : CrudRepository<BoardPostEntity, Long> {

    fun findByUserId(userId: Long, pageable: Pageable): Page<BoardPostEntity>

    @Query("""
    SELECT * FROM board_posts
    WHERE (created_at < :cursorCreatedAt OR (created_at = :cursorCreatedAt AND id < :cursorId))
    ORDER BY created_at DESC, id DESC
    LIMIT :size
""", nativeQuery = true)
    fun findAllByCursorWithCursor(
        @Param("cursorCreatedAt") cursorCreatedAt: LocalDateTime,
        @Param("cursorId") cursorId: Long,
        @Param("size") size: Int
    ): List<BoardPostEntity>

    @Query("""
    SELECT * FROM board_posts
    ORDER BY created_at DESC, id DESC
    LIMIT :size
""", nativeQuery = true)
    fun findAllByCursorNoCursor(
        @Param("size") size: Int
    ): List<BoardPostEntity>

    @Query(
        """
        SELECT * FROM board_posts
        WHERE category = :category
        AND (:hasCursor = false OR (created_at < :cursorCreatedAt OR (created_at = :cursorCreatedAt AND id < :cursorId)))
        ORDER BY created_at DESC, id DESC
        LIMIT CAST(:size AS INTEGER)
        """,
        nativeQuery = true
    )
    fun findByCategoryOrderByCreatedAtDesc(
        @Param("category") category: String,
        @Param("hasCursor") hasCursor: Boolean,
        @Param("cursorCreatedAt") cursorCreatedAt: LocalDateTime?,
        @Param("cursorId") cursorId: Long?,
        @Param("size") size: Int
    ): List<BoardPostEntity>

    @Query(
        """
        SELECT * FROM board_posts
        WHERE (
            (:field = 'title' AND title ILIKE %:keyword%)
            OR (:field = 'content' AND content ILIKE %:keyword%)
            OR (:field = 'nickname' AND nickname ILIKE %:keyword%)
        )
        AND (:hasCursor = false OR (created_at < :cursorCreatedAt OR (created_at = :cursorCreatedAt AND id < :cursorId)))
        ORDER BY created_at DESC, id DESC
        LIMIT CAST(:size AS INTEGER)
        """,
        nativeQuery = true
    )
    fun findByKeyword(
        @Param("field") field: String,
        @Param("keyword") keyword: String,
        @Param("hasCursor") hasCursor: Boolean,
        @Param("cursorCreatedAt") cursorCreatedAt: LocalDateTime?,
        @Param("cursorId") cursorId: Long?,
        @Param("size") size: Int
    ): List<BoardPostEntity>

    @Query(
        """
        SELECT * FROM board_posts
        WHERE category = :category
        AND (
            (:field = 'title' AND title ILIKE %:keyword%)
            OR (:field = 'content' AND content ILIKE %:keyword%)
            OR (:field = 'nickname' AND nickname ILIKE %:keyword%)
        )
        AND (:hasCursor = false OR (created_at < :cursorCreatedAt OR (created_at = :cursorCreatedAt AND id < :cursorId)))
        ORDER BY created_at DESC, id DESC
        LIMIT CAST(:size AS INTEGER)
        """,
        nativeQuery = true
    )
    fun findByCategoryAndKeyword(
        @Param("category") category: String,
        @Param("keyword") keyword: String,
        @Param("field") field: String,
        @Param("hasCursor") hasCursor: Boolean,
        @Param("cursorCreatedAt") cursorCreatedAt: LocalDateTime?,
        @Param("cursorId") cursorId: Long?,
        @Param("size") size: Int
    ): List<BoardPostEntity>

    @Query(
        """
        SELECT * FROM (
            SELECT *, (
                6371 * acos(
                    cos(radians(:latitude)) * cos(radians(latitude)) *
                    cos(radians(longitude) - radians(:longitude)) +
                    sin(radians(:latitude)) * sin(radians(latitude))
                )
            ) AS distance
            FROM board_posts
            WHERE latitude BETWEEN :latMin AND :latMax
              AND longitude BETWEEN :lngMin AND :lngMax
              AND (:hasCursor = false OR (created_at < :cursorCreatedAt OR (created_at = :cursorCreatedAt AND id < :cursorId)))
            ORDER BY created_at DESC, id DESC
            LIMIT CAST(:size AS INTEGER)
        ) AS sub
        WHERE distance <= :radius
        """,
        nativeQuery = true
    )
    fun findWithinDistance(
        @Param("latitude") latitude: Double,
        @Param("longitude") longitude: Double,
        @Param("latMin") latMin: Double,
        @Param("latMax") latMax: Double,
        @Param("lngMin") lngMin: Double,
        @Param("lngMax") lngMax: Double,
        @Param("radius") radius: Double,
        @Param("hasCursor") hasCursor: Boolean,
        @Param("cursorCreatedAt") cursorCreatedAt: LocalDateTime?,
        @Param("cursorId") cursorId: Long?,
        @Param("size") size: Int
    ): List<BoardPostEntity>

    @Query(
        """
        SELECT * FROM (
            SELECT *, (
                6371 * acos(
                    cos(radians(:latitude)) * cos(radians(latitude)) *
                    cos(radians(longitude) - radians(:longitude)) +
                    sin(radians(:latitude)) * sin(radians(latitude))
                )
            ) AS distance
            FROM board_posts
            WHERE latitude BETWEEN :latMin AND :latMax
              AND longitude BETWEEN :lngMin AND :lngMax
              AND category = :category
              AND (:hasCursor = false OR (created_at < :cursorCreatedAt OR (created_at = :cursorCreatedAt AND id < :cursorId)))
            ORDER BY created_at DESC, id DESC
            LIMIT CAST(:size AS INTEGER)
        ) AS sub
        WHERE distance <= :radius
        """,
        nativeQuery = true
    )
    fun findWithinDistanceAndCategory(
        @Param("latitude") latitude: Double,
        @Param("longitude") longitude: Double,
        @Param("latMin") latMin: Double,
        @Param("latMax") latMax: Double,
        @Param("lngMin") lngMin: Double,
        @Param("lngMax") lngMax: Double,
        @Param("radius") radius: Double,
        @Param("category") category: String,
        @Param("hasCursor") hasCursor: Boolean,
        @Param("cursorCreatedAt") cursorCreatedAt: LocalDateTime?,
        @Param("cursorId") cursorId: Long?,
        @Param("size") size: Int
    ): List<BoardPostEntity>

    @Query(
        """
        SELECT * FROM (
            SELECT *, (
                6371 * acos(
                    cos(radians(:latitude)) * cos(radians(latitude)) *
                    cos(radians(longitude) - radians(:longitude)) +
                    sin(radians(:latitude)) * sin(radians(latitude))
                )
            ) AS distance
            FROM board_posts
            WHERE latitude BETWEEN :latMin AND :latMax
              AND longitude BETWEEN :lngMin AND :lngMax
              AND (
                  (:field = 'title' AND title ILIKE %:keyword%)
                  OR (:field = 'content' AND content ILIKE %:keyword%)
                  OR (:field = 'nickname' AND nickname ILIKE %:keyword%)
              )
              AND (:hasCursor = false OR (created_at < :cursorCreatedAt OR (created_at = :cursorCreatedAt AND id < :cursorId)))
            ORDER BY created_at DESC, id DESC
            LIMIT CAST(:size AS INTEGER)
        ) AS sub
        WHERE distance <= :radius
        """,
        nativeQuery = true
    )
    fun findWithinDistanceAndKeyword(
        @Param("latitude") latitude: Double,
        @Param("longitude") longitude: Double,
        @Param("latMin") latMin: Double,
        @Param("latMax") latMax: Double,
        @Param("lngMin") lngMin: Double,
        @Param("lngMax") lngMax: Double,
        @Param("radius") radius: Double,
        @Param("keyword") keyword: String,
        @Param("field") field: String,
        @Param("hasCursor") hasCursor: Boolean,
        @Param("cursorCreatedAt") cursorCreatedAt: LocalDateTime?,
        @Param("cursorId") cursorId: Long?,
        @Param("size") size: Int
    ): List<BoardPostEntity>

    @Query(
        """
        SELECT * FROM (
            SELECT *, (
                6371 * acos(
                    cos(radians(:latitude)) * cos(radians(latitude)) *
                    cos(radians(longitude) - radians(:longitude)) +
                    sin(radians(:latitude)) * sin(radians(latitude))
                )
            ) AS distance
            FROM board_posts
            WHERE latitude BETWEEN :latMin AND :latMax
              AND longitude BETWEEN :lngMin AND :lngMax
              AND category = :category
              AND (
                  (:field = 'title' AND title ILIKE %:keyword%)
                  OR (:field = 'content' AND content ILIKE %:keyword%)
                  OR (:field = 'nickname' AND nickname ILIKE %:keyword%)
              )
              AND (:hasCursor = false OR (created_at < :cursorCreatedAt OR (created_at = :cursorCreatedAt AND id < :cursorId)))
            ORDER BY created_at DESC, id DESC
            LIMIT CAST(:size AS INTEGER)
        ) AS sub
        WHERE distance <= :radius
        """,
        nativeQuery = true
    )
    fun findWithinDistanceCategoryAndKeyword(
        @Param("latitude") latitude: Double,
        @Param("longitude") longitude: Double,
        @Param("latMin") latMin: Double,
        @Param("latMax") latMax: Double,
        @Param("lngMin") lngMin: Double,
        @Param("lngMax") lngMax: Double,
        @Param("radius") radius: Double,
        @Param("category") category: String,
        @Param("keyword") keyword: String,
        @Param("field") field: String,
        @Param("hasCursor") hasCursor: Boolean,
        @Param("cursorCreatedAt") cursorCreatedAt: LocalDateTime?,
        @Param("cursorId") cursorId: Long?,
        @Param("size") size: Int
    ): List<BoardPostEntity>
}
