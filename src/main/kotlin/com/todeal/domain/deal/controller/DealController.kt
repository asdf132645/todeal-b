package com.todeal.domain.deal.controller

import com.todeal.domain.deal.dto.*
import com.todeal.domain.deal.mapper.toResponse
import com.todeal.domain.deal.service.DealService
import com.todeal.global.response.ApiResponse
import org.springframework.web.bind.annotation.*
import com.todeal.domain.deal.repository.DealRepository
import com.todeal.domain.deal.repository.getByIdOrThrow

@RestController
@RequestMapping("/api/deals")
class DealController(
    private val dealService: DealService,
    private val dealRepository: DealRepository

) {

    /** 🔥 딜 생성 시 사용자 ID도 함께 전달 */
    @PostMapping
    fun create(
        @RequestHeader("X-USER-ID") userId: Long,
        @RequestBody request: DealRequest
    ): ApiResponse<DealDto> {
        val result = dealService.createDeal(userId, request)
        return ApiResponse.success(result)
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): ApiResponse<Map<String, Any>> {
        val deal = dealRepository.getByIdOrThrow(id)
        return ApiResponse.success(deal.toResponse()) // ✅ 여기!
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

    @GetMapping("/search")
    fun searchDeals(
        @RequestParam type: String,
        @RequestParam(required = false) keyword: String?,
        @RequestParam(defaultValue = "1") page: Int
    ): ApiResponse<List<DealResponse>> {
        val results = dealService.searchDealsByTypeAndKeyword(type, keyword, page)
        return ApiResponse.success(results)
    }
}
