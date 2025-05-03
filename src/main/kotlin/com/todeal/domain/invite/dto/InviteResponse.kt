package com.todeal.domain.invite.dto

import com.todeal.domain.invite.entity.InviteEntity

data class InviteResponse(
    val id: Long,
    val inviterId: Long,
    val inviteeEmail: String
) {
    companion object {
        fun from(entity: InviteEntity): InviteResponse {
            return InviteResponse(
                id = entity.id,
                inviterId = entity.inviterId,
                inviteeEmail = entity.inviteeEmail
            )
        }
    }
}
