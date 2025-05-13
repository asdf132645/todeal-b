package com.todeal.domain.userTicket.service

import com.todeal.domain.user.repository.UserRepository
import com.todeal.domain.userTicket.dto.DealCheckResponse
import com.todeal.domain.userTicket.dto.UserTicketResponse
import com.todeal.domain.userTicket.entity.UserTicketEntity
import com.todeal.domain.userTicket.repository.UserTicketRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserTicketService(
    private val userRepository: UserRepository,
    private val userTicketRepository: UserTicketRepository
) {

    fun checkDealRegisterable(userId: Long): DealCheckResponse {
        val user = userRepository.findById(userId).orElseThrow { RuntimeException("유저 없음") }

        if (user.isPremium) {
            return DealCheckResponse(canRegister = true, adRequired = false)
        }

        val ticket = userTicketRepository.findByUserId(userId)
            ?: userTicketRepository.save(UserTicketEntity(userId = userId))

        if (ticket.adRequired || ticket.remaining <= 0) {
            ticket.adRequired = true
            userTicketRepository.save(ticket)
            return DealCheckResponse(canRegister = false, adRequired = true)
        }

        return DealCheckResponse(canRegister = true, adRequired = false)
    }

    @Transactional
    fun useDealTicket(userId: Long) {
        val user = userRepository.findById(userId).orElseThrow { RuntimeException("유저 없음") }
        if (user.isPremium) return

        val ticket = userTicketRepository.findByUserId(userId)
            ?: throw RuntimeException("딜 등록 가능 여부 먼저 확인 필요")

        if (ticket.adRequired || ticket.remaining <= 0) {
            throw RuntimeException("광고 시청 후 등록 가능합니다")
        }

        ticket.remaining -= 1
        userTicketRepository.save(ticket)
    }

    @Transactional
    fun completeAdView(userId: Long) {
        val ticket = userTicketRepository.findByUserId(userId)
            ?: userTicketRepository.save(UserTicketEntity(userId = userId))

        ticket.remaining = 10
        ticket.adRequired = false
        userTicketRepository.save(ticket)
    }

    fun getUserTicket(userId: Long): UserTicketResponse {
        val ticket = userTicketRepository.findByUserId(userId)
            ?: userTicketRepository.save(UserTicketEntity(userId = userId))
        return UserTicketResponse.from(ticket)
    }
}