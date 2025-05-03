package com.todeal.domain.userTicket.dto

import com.todeal.domain.userTicket.entity.UserTicketEntity

data class UserTicketResponse(
    val id: Long,
    val type: String,
    val remaining: Int
) {
    companion object {
        fun from(entity: UserTicketEntity): UserTicketResponse = UserTicketResponse(
            id = entity.id,
            type = entity.type,
            remaining = entity.remaining
        )
    }
}