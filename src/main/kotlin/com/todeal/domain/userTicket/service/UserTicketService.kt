package com.todeal.domain.userTicket.service

import com.todeal.domain.userTicket.dto.UserTicketResponse
import com.todeal.domain.userTicket.entity.UserTicketEntity
import com.todeal.domain.userTicket.repository.UserTicketRepository
import org.springframework.stereotype.Service

@Service
class UserTicketService(
    private val userTicketRepository: UserTicketRepository
) {
    fun getUserTickets(userId: Long): List<UserTicketResponse> {
        return userTicketRepository.findAllByUserId(userId).map { UserTicketResponse.from(it) }
    }
}

