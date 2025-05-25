package com.todeal.domain.deal.controller

import com.todeal.domain.deal.dto.*
import com.todeal.domain.deal.mapper.toResponse
import com.todeal.domain.deal.service.DealService
import com.todeal.global.response.ApiResponse
import org.springframework.web.bind.annotation.*
import com.todeal.domain.deal.repository.DealRepository
import com.todeal.domain.deal.repository.getByIdOrThrow

@RestController
@RequestMapping("/deals")
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

    /** 딜 삭제 (거래종료 처리) */
    @DeleteMapping("/{id}")
    fun deleteDeal(
        @RequestHeader("X-USER-ID") userId: Long,
        @PathVariable id: Long
    ): ApiResponse<Unit> {
        dealService.deleteDealWithChats(userId, id)
        return ApiResponse.success(Unit)
    }

    /** 딜 상세 조회 */
    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): ApiResponse<Map<String, Any>> {
        val deal = dealRepository.getByIdOrThrow(id)
        return ApiResponse.success(deal.toResponse())
    }

    /** 필터링된 딜 목록 조회 */
    @GetMapping
    fun getFilteredDeals(
        @RequestParam type: String?,
        @RequestParam(required = false) hashtags: List<String>?,
        @RequestParam(defaultValue = "created") sort: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(required = false) lat: Double?,
        @RequestParam(required = false) lng: Double?,
        @RequestParam(defaultValue = "2") radius: Int
    ): ApiResponse<List<Map<String, Any>>> {
        val result = dealService.getFilteredDeals(type, hashtags, sort, page, size, lat, lng, radius)
        return ApiResponse.success(result.map { it.toResponse() })
    }


    /** 내가 등록한 딜 목록 조회 */
    @GetMapping("/mine")
    fun getMyDeals(@RequestHeader("X-USER-ID") userId: Long): ApiResponse<List<DealResponse>> {
        val deals = dealService.getDealsByUserId(userId)
        return ApiResponse.success(deals)
    }

    /** 제목과 타입을 기반으로 딜 검색 (+ 제외 키워드) */
    @GetMapping("/search")
    fun searchDeals(
        @RequestParam type: String,
        @RequestParam(required = false) keyword: String?,
        @RequestParam(required = false) exclude: String?, // ✅ 추가됨
        @RequestParam(defaultValue = "1") page: Int
    ): ApiResponse<List<DealResponse>> {
        val results = dealService.searchDealsByTypeAndKeyword(type, keyword, exclude, page)
        return ApiResponse.success(results)
    }

    /** 딜 수정 */
    @PutMapping("/{id}")
    fun updateDeal(
        @RequestHeader("X-USER-ID") userId: Long,
        @PathVariable id: Long,
        @RequestBody request: DealRequest
    ): ApiResponse<DealDto> {
        val result = dealService.updateDeal(userId, id, request)
        return ApiResponse.success(result)
    }
}
