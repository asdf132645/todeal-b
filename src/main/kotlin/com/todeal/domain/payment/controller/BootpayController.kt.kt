package com.todeal.domain.payment.controller

import com.todeal.domain.payment.service.BootpayService
import com.todeal.global.response.ApiResponse
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/payments")
class BootpayController(
    private val bootpayService: BootpayService
) {

    @PostMapping("/token")
    fun getAccessToken(): ApiResponse<String> {
        val token = bootpayService.getAccessToken()
        return ApiResponse.success(token)
    }

    @PostMapping("/verify")
    fun verifyReceipt(
        @RequestHeader("X-USER-ID") userId: Long,
        @RequestParam receiptId: String
    ): ApiResponse<Boolean> {
        val verified = bootpayService.verifyReceipt(receiptId, userId)
        return ApiResponse.success(verified)
    }

    @PostMapping("/webhook")
    fun handleWebhook(
        @RequestBody payload: Map<String, Any>
    ): ApiResponse<out String> {
        val receiptId = payload["receipt_id"]?.toString() ?: return ApiResponse.fail("receipt_id 누락")
        val userId = payload["user_id"]?.toString()?.toLongOrNull() ?: return ApiResponse.fail("user_id 누락")

        val verified = bootpayService.verifyReceipt(receiptId, userId)
        return if (verified) {
            ApiResponse.success("결제 검증 및 저장 완료")
        } else {
            ApiResponse.fail("결제 검증 실패")
        }
    }

}
