package com.todeal.domain.deal.repository

import com.todeal.domain.deal.entity.DealEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDateTime

interface DealRepository : JpaRepository<DealEntity, Long> {

    @Query(
        value = """
            SELECT * FROM deals
            WHERE type = :type
            ORDER BY created_at DESC
            OFFSET :offset LIMIT 20
        """,
        nativeQuery = true
    )
    fun findByType(
        @Param("type") type: String,
        @Param("offset") offset: Int
    ): List<DealEntity>

    @Query(
        value = """
            SELECT * FROM deals
            WHERE type = :type
              AND title ILIKE %:keyword%
            ORDER BY created_at DESC
            OFFSET :offset LIMIT 20
        """,
        nativeQuery = true
    )
    fun findByTypeAndKeyword(
        @Param("type") type: String,
        @Param("keyword") keyword: String,
        @Param("offset") offset: Int
    ): List<DealEntity>

    // ✅ NEW: type + 제외 키워드 검색
    @Query(
        value = """
            SELECT * FROM deals
            WHERE type = :type
              AND title NOT ILIKE %:exclude%
            ORDER BY created_at DESC
            OFFSET :offset LIMIT 20
        """,
        nativeQuery = true
    )
    fun findByTypeExcludingKeyword(
        @Param("type") type: String,
        @Param("exclude") exclude: String,
        @Param("offset") offset: Int
    ): List<DealEntity>

    // ✅ NEW: type + 포함 키워드 + 제외 키워드 검색
    @Query(
        value = """
            SELECT * FROM deals
            WHERE type = :type
              AND title ILIKE %:keyword%
              AND title NOT ILIKE %:exclude%
            ORDER BY created_at DESC
            OFFSET :offset LIMIT 20
        """,
        nativeQuery = true
    )
    fun findByTypeAndKeywordExcluding(
        @Param("type") type: String,
        @Param("keyword") keyword: String,
        @Param("exclude") exclude: String,
        @Param("offset") offset: Int
    ): List<DealEntity>

    // ✅ 내가 등록한 딜 목록
    fun findByUserId(userId: Long): List<DealEntity>

    // ✅ 여러 dealId로 한 번에 조회
    fun findByIdIn(ids: Set<Long>): List<DealEntity>

    fun findAllByDeadlineBefore(threshold: LocalDateTime): List<DealEntity>
}
