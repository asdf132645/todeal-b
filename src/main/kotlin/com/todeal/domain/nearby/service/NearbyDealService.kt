// ✅ NearbyDealService.kt
package com.todeal.domain.nearby.service

import com.todeal.domain.deal.entity.DealEntity
import com.todeal.domain.nearby.repository.NearbyDealQueryRepository
import org.springframework.stereotype.Service

@Service
class NearbyDealService(
    private val nearbyDealQueryRepository: NearbyDealQueryRepository
) {
    fun getNearbyDeals(
        lat: Double,
        lng: Double,
        type: String? = null,
        radius: Double = 10.0
    ): List<DealEntity> {
        return if (!type.isNullOrBlank()) {
            nearbyDealQueryRepository.findWithinDistanceAndType(lat, lng, radius, type)
        } else {
            nearbyDealQueryRepository.findWithinDistance(lat, lng, radius)
        }
    }
}