// ✅ UserLocationRepository.kt
package com.todeal.domain.userlocation

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.transaction.annotation.Transactional

interface UserLocationRepository : JpaRepository<UserLocationEntity, Long> {

    @Modifying
    @Transactional
    @Query(
        """
        INSERT INTO user_location (user_id, latitude, longitude)
        VALUES (:userId, :lat, :lng)
        ON CONFLICT (user_id) DO UPDATE
        SET latitude = EXCLUDED.latitude,
            longitude = EXCLUDED.longitude,
            updated_at = CURRENT_TIMESTAMP
        """,
        nativeQuery = true
    )
    fun upsertLocation(
        @Param("userId") userId: Long,
        @Param("lat") lat: Double,
        @Param("lng") lng: Double
    )

    fun findByUserId(userId: Long): UserLocationEntity?
}
