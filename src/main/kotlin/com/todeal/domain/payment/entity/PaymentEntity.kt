package com.todeal.domain.payment.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "payments")
data class PaymentEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val userId: Long,

    val amount: Int,

    val type: String, // 예: "bootpay"

    val method: String? = null, // 카드, 계좌이체 등

    val receiptId: String,

    val status: String, // 예: "success"

    val createdAt: LocalDateTime
)
