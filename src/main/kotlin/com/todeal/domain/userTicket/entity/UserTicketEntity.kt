// UserTicketEntity.kt
package com.todeal.domain.userTicket.entity

import jakarta.persistence.*
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime

@Entity
@Table(name = "user_ticket")
data class UserTicketEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val userId: Long,

    var remaining: Int = 10,

    var adRequired: Boolean = false,

    var type: String = "free",

    @UpdateTimestamp
    var updatedAt: LocalDateTime? = null
)

