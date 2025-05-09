package com.todeal.domain.bid.repository

import com.todeal.domain.bid.entity.BidEntity
import org.springframework.data.jpa.repository.JpaRepository

interface BidRepository : JpaRepository<BidEntity, Long> {

    fun findByDealId(dealId: Long): List<BidEntity>

    fun findByUserIdOrderByCreatedAtDesc(userId: Long): List<BidEntity>

    fun findByDealIdIn(dealIds: List<Long>): List<BidEntity>
}
