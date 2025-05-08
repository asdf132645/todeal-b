// ✅ DealController.kt
package com.todeal.domain.deal.controller

import com.todeal.domain.deal.dto.*
import com.todeal.domain.deal.entity.toResponse
import com.todeal.domain.deal.service.DealService
import com.todeal.global.response.ApiResponse
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/deals")
class DealController(
    private val dealService: DealService
) {

    @PostMapping
    fun create(@RequestBody request: DealRequest): ApiResponse<DealDto> {
        val result = dealService.createDeal(request)
        return ApiResponse.success(result)
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): ApiResponse<DealDto> {
        val result = dealService.getDealById(id)
        return ApiResponse.success(result)
    }

    @GetMapping
    fun getFilteredDeals(
        @RequestParam type: String?,
        @RequestParam(required = false) hashtags: List<String>?,
        @RequestParam(defaultValue = "created") sort: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(required = false) lat: Double?,
        @RequestParam(required = false) lng: Double?
    ): ApiResponse<List<Map<String, Any>>> {
        val result = dealService.getFilteredDeals(type, hashtags, sort, page, size, lat, lng)
        return ApiResponse.success(result.map { it.toResponse() })
    }
}