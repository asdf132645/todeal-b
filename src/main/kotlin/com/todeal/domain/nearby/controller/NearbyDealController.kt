// ✅ NearbyDealController.kt
package com.todeal.domain.nearby.controller

import com.todeal.domain.deal.mapper.toResponse
import com.todeal.domain.nearby.service.NearbyDealService
import com.todeal.domain.userlocation.UserLocationService
import com.todeal.global.response.ApiResponse
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/deals")
class NearbyDealController(
    private val nearbyDealService: NearbyDealService,
    private val userLocationService: UserLocationService
) {

    @GetMapping("/nearby")
    fun getNearbyDeals(
        @RequestHeader(name = "X-USER-ID", required = false) userId: Long?,
        @RequestParam(required = false) lat: Double?,
        @RequestParam(required = false) lng: Double?,
        @RequestParam(required = false) type: String?,
        @RequestParam(defaultValue = "10.0") radius: Double
    ): ApiResponse<List<Map<String, Any>>> {
        val (latitude, longitude) = if (userId != null) {
            val location = userLocationService.getUserLocation(userId)
            location.latitude to location.longitude
        } else if (lat != null && lng != null) {
            lat to lng
        } else {
            throw IllegalArgumentException("사용자 ID 또는 위도/경도 좌표를 제공해야 합니다.")
        }

        val deals = nearbyDealService.getNearbyDeals(latitude, longitude, type, radius)
        return ApiResponse.success(deals.map { it.toResponse() })
    }


    @GetMapping("/nearby/by-coord")
    fun getNearbyDealsByCoord(
        @RequestParam lat: Double,
        @RequestParam lng: Double,
        @RequestParam type: String
    ): ApiResponse<List<Map<String, Any>>> {
        val deals = nearbyDealService.getNearbyDeals(lat, lng, type)
        return ApiResponse.success(deals.map { it.toResponse() })
    }
}
