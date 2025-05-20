package com.todeal.domain.customerSupport.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "customer_support")
data class CustomerSupportEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val userId: Long,

    @Column(nullable = false)
    val title: String,

    @Column(nullable = false, columnDefinition = "TEXT")
    val content: String,

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var status: Status = Status.PENDING,

    var adminReply: String? = null
) {
    enum class Status {
        PENDING, ANSWERED
    }
}
