package com.todeal.domain.invite.service

import com.todeal.domain.invite.dto.InviteDto
import com.todeal.domain.invite.dto.InviteResponse
import com.todeal.domain.invite.entity.InviteEntity
import com.todeal.domain.invite.repository.InviteRepository
import org.springframework.stereotype.Service

@Service
class InviteService(
    private val inviteRepository: InviteRepository
) {
    fun create(dto: InviteDto): InviteResponse {
        val saved = inviteRepository.save(
            InviteEntity(
                inviterId = dto.inviterId,
                inviteeEmail = dto.inviteeEmail
            )
        )
        return InviteResponse.from(saved)
    }

    fun getAll(): List<InviteResponse> {
        return inviteRepository.findAll().map { InviteResponse.from(it) }
    }
}
