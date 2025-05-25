package com.todeal.domain.deal.repository

import com.todeal.domain.deal.entity.DealEntity
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.stereotype.Repository

@Repository
class DealQueryRepository {

    @PersistenceContext
    private lateinit var em: EntityManager

    fun findFilteredDeals(
        type: String?,
        hashtags: List<String>?,
        sort: String,
        page: Int,
        size: Int,
        lat: Double?,
        lng: Double?,
        radius: Int
    ): List<DealEntity> {
        val sb = StringBuilder()
        sb.append("SELECT d FROM DealEntity d ")

        if (!hashtags.isNullOrEmpty()) {
            sb.append("JOIN d.hashtags h ")
        }

        sb.append("WHERE d.deadline > CURRENT_TIMESTAMP ")

        if (!type.isNullOrBlank()) {
            sb.append("AND d.type = :type ")
        }

        if (!hashtags.isNullOrEmpty()) {
            sb.append("AND h.name IN :hashtags ")
        }

        // ✅ 거리 필터링은 항상 적용 (lat/lng 둘 다 존재 시)
        if (lat != null && lng != null) {
            sb.append(
                "AND (6371 * acos(cos(radians(:lat)) * cos(radians(d.latitude)) * " +
                        "cos(radians(d.longitude) - radians(:lng)) + sin(radians(:lat)) * sin(radians(d.latitude)))) <= :radius "
            )
        }

        // ✅ 정렬 조건 분기
        when (sort) {
            "distance" -> {
                if (lat != null && lng != null) {
                    sb.append(
                        "ORDER BY (6371 * acos(cos(radians(:lat)) * cos(radians(d.latitude)) * " +
                                "cos(radians(d.longitude) - radians(:lng)) + sin(radians(:lat)) * sin(radians(d.latitude)))) ASC "
                    )
                } else {
                    sb.append("ORDER BY d.createdAt DESC ")
                }
            }

            "deadline" -> sb.append("ORDER BY d.deadline ASC ")
            else -> sb.append("ORDER BY d.createdAt DESC ")
        }

        val query = em.createQuery(sb.toString(), DealEntity::class.java)

        if (!type.isNullOrBlank()) query.setParameter("type", type)
        if (!hashtags.isNullOrEmpty()) query.setParameter("hashtags", hashtags)

        // 거리 필터링이 조건에 포함되었을 경우 좌표 세팅
        if (lat != null && lng != null) {
            query.setParameter("lat", lat)
            query.setParameter("lng", lng)
            query.setParameter("radius", radius)
        }

        query.firstResult = page * size
        query.maxResults = size

        return query.resultList
    }
}
