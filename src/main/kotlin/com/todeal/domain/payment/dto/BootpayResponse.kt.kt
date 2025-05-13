// ✅ BootpayResponse.kt (optional - 향후 확장용)
package com.todeal.domain.payment.dto

data class BootpayResponse(
    val status: String,
    val message: String? = null
)
