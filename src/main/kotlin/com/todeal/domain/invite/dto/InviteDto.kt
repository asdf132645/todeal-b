package com.todeal.domain.invite.dto

data class InviteDto(
    val id: Long,
    val inviterId: Long,
    val inviteeEmail: String
)
