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
                    6371 * 2 * ASIN(SQRT(
                        POWER(SIN(RADIANS(:lat - d.latitude) / 2), 2) +
                        COS(RADIANS(:lat)) * COS(RADIANS(d.latitude)) *
                        POWER(SIN(RADIANS(:lng - d.longitude) / 2), 2)
                    ))
                ) AS distance
                FROM deals d
                WHERE d.deadline > NOW()
            ) AS sub
            WHERE distance <= :radius
            ORDER BY distance ASC
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
                    6371 * 2 * ASIN(SQRT(
                        POWER(SIN(RADIANS(:lat - d.latitude) / 2), 2) +
                        COS(RADIANS(:lat)) * COS(RADIANS(d.latitude)) *
                        POWER(SIN(RADIANS(:lng - d.longitude) / 2), 2)
                    ))
                ) AS distance
                FROM deals d
                WHERE d.deadline > NOW() AND LOWER(d.type) = LOWER(:type)
            ) AS sub
            WHERE distance <= :radius
            ORDER BY distance ASC
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
