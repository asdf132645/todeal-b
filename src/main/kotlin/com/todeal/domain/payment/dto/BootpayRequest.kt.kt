// ✅ BootpayRequest.kt (optional - 향후 확장용)
package com.todeal.domain.payment.dto

data class BootpayRequest(
    val userId: Long,
    val planId: Int,
    val orderId: String,
    val receiptId: String
)