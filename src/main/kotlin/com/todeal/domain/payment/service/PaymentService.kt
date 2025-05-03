// service/PaymentService.kt
package com.todeal.domain.payment.service

import com.todeal.domain.payment.dto.PaymentRequest
import com.todeal.domain.payment.dto.PaymentResponse
import com.todeal.domain.payment.entity.Payment
import com.todeal.domain.payment.repository.PaymentRepository
import org.springframework.stereotype.Service

@Service
class PaymentService(
    private val paymentRepository: PaymentRepository
) {
    fun createPayment(request: PaymentRequest): PaymentResponse {
        val payment = Payment(
            userId = request.userId,
            amount = request.amount,
            type = request.type,
            method = request.method,
            status = "success" // 임시
        )
        return PaymentResponse.from(paymentRepository.save(payment))
    }

    fun getById(id: Long): PaymentResponse {
        return paymentRepository.findById(id)
            .map { PaymentResponse.from(it) }
            .orElseThrow { NoSuchElementException("결제 내역을 찾을 수 없습니다.") }
    }

    fun getAll(): List<PaymentResponse> {
        return paymentRepository.findAll().map { PaymentResponse.from(it) }
    }
}
