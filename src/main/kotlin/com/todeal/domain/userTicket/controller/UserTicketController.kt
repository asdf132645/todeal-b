package com.todeal.domain.userTicket.controller

import com.todeal.domain.userTicket.dto.UserTicketResponse
import com.todeal.domain.userTicket.service.UserTicketService
import com.todeal.global.response.ApiResponse
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/user-tickets")
class UserTicketController(
    private val userTicketService: UserTicketService
) {

    @GetMapping("/{userId}")
    fun getUserTickets(@PathVariable userId: Long): ApiResponse<List<UserTicketResponse>> {
        val result = userTicketService.getUserTickets(userId)
        return ApiResponse.success(result)
    }
}
