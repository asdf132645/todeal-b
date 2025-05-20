package com.todeal.domain.chat.repository

import com.todeal.domain.chat.entity.ChatRoomEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface ChatRoomRepository : JpaRepository<ChatRoomEntity, Long> {
    fun findByDealId(dealId: Long): ChatRoomEntity?
    fun findAllBySellerIdOrBuyerId(sellerId: Long, buyerId: Long): List<ChatRoomEntity>
    fun findAllByDealId(dealId: Long): List<ChatRoomEntity>
    @Query("""
    SELECT c FROM ChatRoomEntity c 
    WHERE c.dealId = :dealId 
    AND ((c.sellerId = :userId1 AND c.buyerId = :userId2) 
       OR (c.sellerId = :userId2 AND c.buyerId = :userId1))
""")
    fun findByUsersAndDeal(
        @Param("userId1") userId1: Long,
        @Param("userId2") userId2: Long,
        @Param("dealId") dealId: Long
    ): ChatRoomEntity?

}
