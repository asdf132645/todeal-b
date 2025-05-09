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
    fun getNearbyDealsByUserId(
        @RequestHeader("X-USER-ID") userId: Long,
        @RequestParam(required = false) type: String?,
        @RequestParam(required = false, defaultValue = "2.0") radius: Double // 🔥 radius 추가됨
    ): ApiResponse<List<Map<String, Any>>> {
        val location = userLocationService.getUserLocation(userId)
        val deals = nearbyDealService.getNearbyDeals(
            lat = location.latitude,
            lng = location.longitude,
            type = type,
            radius = radius // 🔥 넘겨줌
        )
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
