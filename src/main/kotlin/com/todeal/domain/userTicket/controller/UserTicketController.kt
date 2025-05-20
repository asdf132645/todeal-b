package com.todeal.domain.user.controller

import com.todeal.domain.userTicket.dto.DealCheckResponse
import com.todeal.domain.userTicket.dto.UserTicketResponse
import com.todeal.domain.userTicket.service.UserTicketService
import com.todeal.global.response.ApiResponse
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users")
class UserTicketController(
    private val userTicketService: UserTicketService
) {
    @GetMapping("/deal-check")
    fun checkRegisterable(@RequestHeader("X-USER-ID") userId: Long): ApiResponse<DealCheckResponse> {
        return ApiResponse.success(userTicketService.checkDealRegisterable(userId))
    }

    @PostMapping("/ad-complete")
    fun completeAd(@RequestHeader("X-USER-ID") userId: Long): ApiResponse<String> {
        userTicketService.completeAdView(userId)
        return ApiResponse.success("광고 시청 완료")
    }

    @GetMapping("/ticket")
    fun getTicket(@RequestHeader("X-USER-ID") userId: Long): ApiResponse<UserTicketResponse> {
        return ApiResponse.success(userTicketService.getUserTicket(userId))
    }
}