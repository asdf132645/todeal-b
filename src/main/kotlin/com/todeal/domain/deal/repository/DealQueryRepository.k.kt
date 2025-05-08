package com.todeal.domain.deal.repository

// ✅ DealQueryRepository.kt

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
        sb.append("SELECT d.* FROM deals d ")

        if (!hashtags.isNullOrEmpty()) {
            sb.append("JOIN deal_hashtags h ON d.id = h.deal_id ")
        }

        sb.append("WHERE d.deadline > now() ")

        if (!type.isNullOrBlank()) {
            sb.append("AND d.type = :type ")
        }

        if (!hashtags.isNullOrEmpty()) {
            sb.append("AND h.hashtag IN :hashtags ")
        }

        if (sort == "distance" && lat != null && lng != null) {
            sb.append("ORDER BY (")
            sb.append("6371 * acos(cos(radians(:lat)) * cos(radians(d.latitude)) * ")
            sb.append("cos(radians(d.longitude) - radians(:lng)) + ")
            sb.append("sin(radians(:lat)) * sin(radians(d.latitude)))")
            sb.append(" ASC ")
        } else {
            sb.append("ORDER BY d.created_at DESC ")
        }

        val query = em.createNativeQuery(sb.toString(), DealEntity::class.java)

        if (!type.isNullOrBlank()) query.setParameter("type", type)
        if (!hashtags.isNullOrEmpty()) query.setParameter("hashtags", hashtags)
        if (sort == "distance" && lat != null && lng != null) {
            query.setParameter("lat", lat)
            query.setParameter("lng", lng)
        }

        query.firstResult = page * size
        query.maxResults = size

        return query.resultList as List<DealEntity>
    }
}
