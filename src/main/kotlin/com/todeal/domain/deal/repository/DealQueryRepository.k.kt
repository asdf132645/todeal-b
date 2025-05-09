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
        lng: Double?
    ): List<DealEntity> {
        val sb = StringBuilder()
        sb.append("SELECT d FROM DealEntity d ")

        // 해시태그 필터가 있을 경우 JOIN
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

        // 정렬 기준
        when {
            sort == "distance" && lat != null && lng != null -> {
                sb.append(
                    "ORDER BY " +
                            "(6371 * acos(cos(radians(:lat)) * cos(radians(d.latitude)) * " +
                            "cos(radians(d.longitude) - radians(:lng)) + sin(radians(:lat)) * sin(radians(d.latitude))))"
                )
            }

            sort == "deadline" -> {
                sb.append("ORDER BY d.deadline ASC ")
            }

            else -> {
                sb.append("ORDER BY d.createdAt DESC ")
            }
        }

        val query = em.createQuery(sb.toString(), DealEntity::class.java)

        // 파라미터 바인딩
        if (!type.isNullOrBlank()) query.setParameter("type", type)
        if (!hashtags.isNullOrEmpty()) query.setParameter("hashtags", hashtags)
        if (sort == "distance" && lat != null && lng != null) {
            query.setParameter("lat", lat)
            query.setParameter("lng", lng)
        }

        query.firstResult = page * size
        query.maxResults = size

        return query.resultList
    }
}
