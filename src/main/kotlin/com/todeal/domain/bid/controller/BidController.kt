package com.todeal.domain.bid.controller

import com.todeal.domain.bid.dto.BidDto
import com.todeal.domain.bid.dto.BidRequest
import com.todeal.domain.bid.dto.BidResponse
import com.todeal.domain.bid.service.BidService
import com.todeal.global.response.ApiResponse
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/bids")
class BidController(
    private val bidService: BidService
) {

    @PostMapping
    fun placeBid(@RequestBody request: BidRequest): ApiResponse<BidResponse> {
        val result = bidService.placeBid(request)
        return ApiResponse.success(result)
    }

    @GetMapping("/highest/{dealId}")
    fun getHighest(@PathVariable dealId: Long): ApiResponse<BidDto?> {
        val result = bidService.getHighestBid(dealId)
        return ApiResponse.success(result)
    }
}
