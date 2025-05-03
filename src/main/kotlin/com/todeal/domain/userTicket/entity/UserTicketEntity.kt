// UserTicketEntity.kt
package com.todeal.domain.userTicket.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "user_tickets")
data class UserTicketEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val userId: Long,

    @Column(nullable = false)
    val type: String, // invited_reward, purchase, etc

    @Column(nullable = false)
    var remaining: Int = 0,

    val createdAt: LocalDateTime = LocalDateTime.now()
)
