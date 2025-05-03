package com.todeal.domain.userTicket.repository

import com.todeal.domain.userTicket.entity.UserTicketEntity
import org.springframework.data.jpa.repository.JpaRepository

interface UserTicketRepository : JpaRepository<UserTicketEntity, Long> {
    fun findAllByUserId(userId: Long): List<UserTicketEntity>
}