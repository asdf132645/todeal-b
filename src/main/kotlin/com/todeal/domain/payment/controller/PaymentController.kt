// controller/PaymentController.kt
package com.todeal.domain.payment.controller

import com.todeal.domain.payment.dto.PaymentRequest
import com.todeal.domain.payment.dto.PaymentResponse
import com.todeal.domain.payment.service.PaymentService
import com.todeal.global.response.ApiResponse
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/payments")
class PaymentController(
    private val paymentService: PaymentService
) {

    @PostMapping
    fun create(@RequestBody request: PaymentRequest): ApiResponse<PaymentResponse> {
        val result = paymentService.createPayment(request)
        return ApiResponse.success(result)
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): ApiResponse<PaymentResponse> {
        val result = paymentService.getById(id)
        return ApiResponse.success(result)
    }

    @GetMapping
    fun getAll(): ApiResponse<List<PaymentResponse>> {
        val result = paymentService.getAll()
        return ApiResponse.success(result)
    }
}
