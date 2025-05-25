package com.todeal.domain.bid.controller

import com.todeal.domain.bid.dto.BidRequest
import com.todeal.domain.bid.dto.BidResponse
import com.todeal.domain.bid.dto.BidWithDealDto
import com.todeal.domain.bid.dto.DealBidGroupDto
import com.todeal.domain.bid.service.BidService
import com.todeal.global.response.ApiResponse
import org.springframework.data.domain.Page
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/bids")
class BidController(
    private val bidService: BidService
) {

    @GetMapping
    fun getBidsByDealId(@RequestParam dealId: Long): ApiResponse<List<BidResponse>> {
        val bids = bidService.getBidsByDealId(dealId)
        return ApiResponse.success(bids)
    }

    @PostMapping("/{id}/select-winner")
    fun selectWinner(@PathVariable id: Long): ApiResponse<String> {
        bidService.selectWinnerBid(id)
        return ApiResponse.success("낙찰 완료")
    }

    /** 입찰 등록 */
    @PostMapping
    fun placeBid(@RequestBody request: BidRequest): ApiResponse<String> {
        bidService.placeBid(request)
        return ApiResponse.success("입찰 완료")
    }

    /** 내가 입찰한 딜 리스트 (페이징 + 검색 + 타입 필터링) */
    @GetMapping("/mine")
    fun getMyBids(
        @RequestHeader("X-USER-ID") userId: Long,
        @RequestParam(required = false) type: String?,
        @RequestParam(required = false) keyword: String?,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ApiResponse<Page<Any>> {
        val result = bidService.getMyBids(userId, type, keyword, page, size)
        return ApiResponse.success(result)
    }

    @GetMapping("/on-my-deals")
    fun getBidsOnMyDeals(
        @RequestHeader("X-USER-ID") userId: Long,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(required = false) keyword: String?
    ): ApiResponse<Page<DealBidGroupDto>> {
        val result = bidService.getBidsOnMyDeals(userId, page, size, keyword)
        return ApiResponse.success(result)
    }


    @PatchMapping("/{dealId}/cancel-winner")
    fun cancelWinner(@PathVariable dealId: Long): ApiResponse<String> {
        bidService.cancelWinner(dealId)
        return ApiResponse.success("낙찰자 취소 완료")
    }

    @DeleteMapping("/{bidId}")
    fun cancelBid(@PathVariable bidId: Long): ApiResponse<String> {
        bidService.cancelBid(bidId)
        return ApiResponse.success("입찰 취소 완료")
    }
}