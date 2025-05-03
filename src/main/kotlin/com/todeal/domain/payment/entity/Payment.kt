// entity/Payment.kt
package com.todeal.domain.payment.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "payments")
data class Payment(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val userId: Long,
    val amount: Int,

    val type: String,  // plan / single
    val method: String,  // stripe

    val status: String,  // success / fail
    val createdAt: LocalDateTime = LocalDateTime.now()
)
