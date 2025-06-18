package com.todeal.domain.deal.repository

import com.todeal.domain.deal.entity.DealEntity
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.time.ZoneOffset

@Repository
class DealQueryRepository {

    @PersistenceContext
    private lateinit var em: EntityManager

    fun findFilteredDeals(
        type: String?,
        hashtags: List<String>?,
        sort: String,
        cursor: Long?,
        size: Int,
        lat: Double?,
        lng: Double?,
        radius: Int
    ): List<DealEntity> {
        val sb = StringBuilder()
        sb.append("SELECT d FROM DealEntity d ")

        if (!hashtags.isNullOrEmpty()) sb.append("JOIN d.hashtags h ")

        sb.append("WHERE d.deadline > CURRENT_TIMESTAMP ")

        if (!type.isNullOrBlank()) sb.append("AND d.type = :type ")
        if (!hashtags.isNullOrEmpty()) sb.append("AND h.name IN :hashtags ")

        // ✅ 커서 조건 추가
        cursor?.let {
            sb.append("AND d.createdAt < :cursorTime ")
        }

        if (lat != null && lng != null) {
            sb.append(
                "AND (6371 * acos(cos(radians(:lat)) * cos(radians(d.latitude)) * " +
                        "cos(radians(d.longitude) - radians(:lng)) + sin(radians(:lat)) * sin(radians(d.latitude)))) <= :radius "
            )
        }

        sb.append("ORDER BY d.createdAt DESC ")

        val query = em.createQuery(sb.toString(), DealEntity::class.java)

        if (!type.isNullOrBlank()) query.setParameter("type", type)
        if (!hashtags.isNullOrEmpty()) query.setParameter("hashtags", hashtags)
        if (cursor != null) {
            query.setParameter("cursorTime", LocalDateTime.ofEpochSecond(cursor / 1000, 0, ZoneOffset.UTC))
        }
        if (lat != null && lng != null) {
            query.setParameter("lat", lat)
            query.setParameter("lng", lng)
            query.setParameter("radius", radius)
        }

        query.maxResults = size
        return query.resultList
    }
    fun searchDealsByTypeAndKeywordWithLocation(
        type: String,
        keyword: String?,
        exclude: String?,
        offset: Int,
        limit: Int,  // ✅ 추가: pageSize
        lat: Double?,
        lng: Double?,
        radius: Int
    ): List<DealEntity> {
        val sb = StringBuilder()
        sb.append("SELECT d FROM DealEntity d WHERE d.deadline > CURRENT_TIMESTAMP ")

        if (!type.isNullOrBlank()) {
            sb.append("AND d.type = :type ")
        }

        if (!keyword.isNullOrBlank()) {
            sb.append(
                "AND (" +
                        "LOWER(d.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
                        "OR LOWER(d.description) LIKE LOWER(CONCAT('%', :keyword, '%'))" +
                        ") "
            )
        }

        if (!exclude.isNullOrBlank()) {
            sb.append(
                "AND (" +
                        "LOWER(d.title) NOT LIKE LOWER(CONCAT('%', :exclude, '%')) " +
                        "AND LOWER(d.description) NOT LIKE LOWER(CONCAT('%', :exclude, '%'))" +
                        ") "
            )
        }

        if (lat != null && lng != null) {
            sb.append(
                "AND (6371 * acos(" +
                        "cos(radians(:lat)) * cos(radians(d.latitude)) * " +
                        "cos(radians(d.longitude) - radians(:lng)) + " +
                        "sin(radians(:lat)) * sin(radians(d.latitude))" +
                        ")) <= :radius "
            )
        }

        sb.append("ORDER BY d.createdAt DESC")

        val query = em.createQuery(sb.toString(), DealEntity::class.java)

        if (!type.isNullOrBlank()) query.setParameter("type", type)
        if (!keyword.isNullOrBlank()) query.setParameter("keyword", keyword)
        if (!exclude.isNullOrBlank()) query.setParameter("exclude", exclude)
        if (lat != null && lng != null) {
            query.setParameter("lat", lat)
            query.setParameter("lng", lng)
            query.setParameter("radius", radius)
        }

        query.firstResult = offset
        query.maxResults = limit  // ✅ 고정값 대신 전달된 limit 사용

        return query.resultList
    }
    fun searchByCursor(
        type: String,
        keyword: String?,
        exclude: String?,
        cursorId: Long?,
        limit: Int,
        lat: Double?,
        lng: Double?,
        radius: Int
    ): List<DealEntity> {
        val sb = StringBuilder()
        sb.append("SELECT d FROM DealEntity d WHERE d.deadline > CURRENT_TIMESTAMP ")

        if (!type.isNullOrBlank()) {
            sb.append("AND d.type = :type ")
        }

        if (!keyword.isNullOrBlank()) {
            sb.append("AND (LOWER(d.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
                    "OR LOWER(d.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) ")
        }

        if (!exclude.isNullOrBlank()) {
            sb.append("AND (LOWER(d.title) NOT LIKE LOWER(CONCAT('%', :exclude, '%')) " +
                    "AND LOWER(d.description) NOT LIKE LOWER(CONCAT('%', :exclude, '%'))) ")
        }

        if (lat != null && lng != null) {
            sb.append("AND (6371 * acos(cos(radians(:lat)) * cos(radians(d.latitude)) * " +
                    "cos(radians(d.longitude) - radians(:lng)) + sin(radians(:lat)) * sin(radians(d.latitude)))) <= :radius ")
        }

        if (cursorId != null) {
            sb.append("AND d.id < :cursorId ")
        }

        sb.append("ORDER BY d.id DESC")

        val query = em.createQuery(sb.toString(), DealEntity::class.java)

        if (!type.isNullOrBlank()) query.setParameter("type", type)
        if (!keyword.isNullOrBlank()) query.setParameter("keyword", keyword)
        if (!exclude.isNullOrBlank()) query.setParameter("exclude", exclude)
        if (lat != null && lng != null) {
            query.setParameter("lat", lat)
            query.setParameter("lng", lng)
            query.setParameter("radius", radius)
        }
        if (cursorId != null) query.setParameter("cursorId", cursorId)

        query.maxResults = limit

        return query.resultList
    }

}
