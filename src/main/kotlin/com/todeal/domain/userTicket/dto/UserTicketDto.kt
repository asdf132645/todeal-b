package com.todeal.domain.userTicket.dto

data class UserTicketDto(
    val id: Long,
    val userId: Long,
    val type: String,
    val remaining: Int
)