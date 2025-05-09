package com.todeal.domain.barterBid.controller

import com.todeal.global.response.ApiResponse
import com.todeal.domain.barterBid.dto.BarterBidRequest
import com.todeal.domain.barterBid.dto.BarterBidResponse
import com.todeal.domain.barterBid.service.BarterBidService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/barter-bids")
class BarterBidController(
    private val barterBidService: BarterBidService
) {

    @PostMapping
    fun createBid(
        @RequestHeader("X-USER-ID") userId: Long,
        @RequestBody request: BarterBidRequest
    ): ApiResponse<BarterBidResponse> {
        val result = barterBidService.createBarterBid(userId, request)
        return ApiResponse.success(result)
    }

    @GetMapping("/deal/{dealId}")
    fun getBidsByDeal(@PathVariable dealId: Long): ApiResponse<List<BarterBidResponse>> {
        val results = barterBidService.getBarterBidsByDeal(dealId)
        return ApiResponse.success(results)
    }

    @PatchMapping("/{id}/accept")
    fun acceptBid(@PathVariable id: Long): ApiResponse<String> {
        barterBidService.acceptBid(id)
        return ApiResponse.success("입찰이 수락되었습니다.")
    }

    @PatchMapping("/{id}/reject")
    fun rejectBid(@PathVariable id: Long): ApiResponse<String> {
        barterBidService.rejectBid(id)
        return ApiResponse.success("입찰이 거절되었습니다.")
    }

}
