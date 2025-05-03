// dto/PaymentRequest.kt
package com.todeal.domain.payment.dto

data class PaymentRequest(
    val userId: Long,
    val amount: Int,
    val type: String, // plan / single
    val method: String // stripe
)
