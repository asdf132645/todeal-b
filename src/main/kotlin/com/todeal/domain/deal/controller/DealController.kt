package com.todeal.domain.deal.controller

import com.todeal.domain.deal.dto.*
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
    fun getAll(): ApiResponse<List<DealResponse>> {
        val result = dealService.getAllDeals()
        return ApiResponse.success(result)
    }
}
