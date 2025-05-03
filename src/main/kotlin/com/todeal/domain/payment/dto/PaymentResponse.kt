// dto/PaymentResponse.kt
package com.todeal.domain.payment.dto

import com.todeal.domain.payment.entity.Payment
import java.time.LocalDateTime

data class PaymentResponse(
    val id: Long,
    val userId: Long,
    val amount: Int,
    val type: String,
    val method: String,
    val status: String,
    val createdAt: LocalDateTime
) {
    companion object {
        fun from(payment: Payment): PaymentResponse {
            return PaymentResponse(
                id = payment.id,
                userId = payment.userId,
                amount = payment.amount,
                type = payment.type,
                method = payment.method,
                status = payment.status,
                createdAt = payment.createdAt
            )
        }
    }
}
