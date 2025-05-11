package com.todeal.domain.chat.repository

import com.todeal.domain.chat.entity.ChatRoomEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ChatRoomRepository : JpaRepository<ChatRoomEntity, Long> {
    fun findByDealId(dealId: Long): ChatRoomEntity?
    fun findAllBySellerIdOrBuyerId(sellerId: Long, buyerId: Long): List<ChatRoomEntity>
    fun findAllByDealId(dealId: Long): List<ChatRoomEntity>
}
