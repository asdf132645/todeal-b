package com.todeal.domain.nearby.repository

import com.todeal.domain.deal.entity.DealEntity
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.Repository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository as SpringRepository

@SpringRepository
interface NearbyDealQueryRepository : Repository<DealEntity, Long> {

    @Query(
        value = """
            SELECT * FROM (
                SELECT d.*, (
                    6371 * acos(
                        cos(radians(:lat)) * cos(radians(d.latitude)) *
                        cos(radians(d.longitude) - radians(:lng)) +
                        sin(radians(:lat)) * sin(radians(d.latitude))
                    )
                ) AS distance
                FROM deals d
                WHERE d.deadline > now()
            ) AS sub
            WHERE distance < :radius
            ORDER BY sub.created_at DESC
            LIMIT 4
        """,
        nativeQuery = true
    )
    fun findWithinDistance(
        @Param("lat") lat: Double,
        @Param("lng") lng: Double,
        @Param("radius") radius: Double
    ): List<DealEntity>

    @Query(
        value = """
            SELECT * FROM (
                SELECT d.*, (
                    6371 * acos(
                        cos(radians(:lat)) * cos(radians(d.latitude)) *
                        cos(radians(d.longitude) - radians(:lng)) +
                        sin(radians(:lat)) * sin(radians(d.latitude))
                    )
                ) AS distance
                FROM deals d
                WHERE d.deadline > now() AND LOWER(d.type) = LOWER(:type)
            ) AS sub
            WHERE distance < :radius
            ORDER BY sub.created_at DESC
            LIMIT 4
        """,
        nativeQuery = true
    )
    fun findWithinDistanceAndType(
        @Param("lat") lat: Double,
        @Param("lng") lng: Double,
        @Param("radius") radius: Double,
        @Param("type") type: String
    ): List<DealEntity>
}
