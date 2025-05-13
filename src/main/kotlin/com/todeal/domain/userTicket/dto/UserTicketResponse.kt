package com.todeal.domain.userTicket.dto

import com.todeal.domain.userTicket.entity.UserTicketEntity
import java.time.LocalDateTime

data class UserTicketResponse(
    val id: Long,
    val type: String,
    val remaining: Int,
    val adRequired: Boolean,
    val updatedAt: LocalDateTime?
) {
    companion object {
        fun from(entity: UserTicketEntity): UserTicketResponse = UserTicketResponse(
            id = entity.id,
            type = entity.type,
            remaining = entity.remaining,
            adRequired = entity.adRequired,
            updatedAt = entity.updatedAt
        )
    }
}