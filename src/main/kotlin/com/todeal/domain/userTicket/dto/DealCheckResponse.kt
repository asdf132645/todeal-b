package com.todeal.domain.userTicket.dto

data class DealCheckResponse(
    val canRegister: Boolean,
    val adRequired: Boolean
)