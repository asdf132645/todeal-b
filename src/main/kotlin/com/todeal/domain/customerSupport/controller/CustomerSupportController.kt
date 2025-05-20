package com.todeal.domain.customerSupport.controller

import com.todeal.domain.customerSupport.dto.CustomerSupportRequest
import com.todeal.domain.customerSupport.service.CustomerSupportService
import com.todeal.global.response.ApiResponse
import com.todeal.domain.user.repository.UserRepository
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/support")
class CustomerSupportController(
    private val service: CustomerSupportService,
    private val userRepository: UserRepository
) {

    @PostMapping
    fun submit(
        @RequestHeader("X-USER-ID") userId: Long,
        @RequestBody request: CustomerSupportRequest
    ): ApiResponse<Any> {
        return ApiResponse.success(service.submitInquiry(userId, request))
    }

    @GetMapping("/me")
    fun getMyInquiries(
        @RequestHeader("X-USER-ID") userId: Long
    ): ApiResponse<Any> {
        return ApiResponse.success(service.getMyInquiries(userId))
    }

    @GetMapping("/{id}")
    fun getOne(
        @RequestHeader("X-USER-ID") userId: Long,
        @PathVariable id: Long
    ): ApiResponse<Any> {
        return ApiResponse.success(service.getInquiry(id)) // 필요한 경우 관리자 여부 확인 추가
    }

    @GetMapping("/admin")
    fun getAll(
        @RequestHeader("X-USER-ID") userId: Long
    ): ApiResponse<out Any> {
        if (!isAdmin(userId)) {
            return ApiResponse.fail("관리자만 접근할 수 있습니다.")
        }
        return ApiResponse.success(service.getAll())
    }

    @PatchMapping("/{id}/reply")
    fun reply(
        @RequestHeader("X-USER-ID") userId: Long,
        @PathVariable id: Long,
        @RequestParam reply: String
    ): ApiResponse<out Any> {
        if (!isAdmin(userId)) {
            return ApiResponse.fail("관리자만 접근할 수 있습니다.")
        }
        return ApiResponse.success(service.replyToInquiry(id, reply))
    }

    private fun isAdmin(userId: Long): Boolean {
        val user = userRepository.findById(userId).orElse(null)
        return user?.role == "admin"
    }
}
