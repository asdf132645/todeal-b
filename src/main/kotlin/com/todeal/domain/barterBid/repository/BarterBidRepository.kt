package com.todeal.domain.barterBid.repository

import com.todeal.domain.barterBid.entity.BarterBidEntity
import org.springframework.data.jpa.repository.JpaRepository

interface BarterBidRepository : JpaRepository<BarterBidEntity, Long> {
    fun findByDealId(dealId: Long): List<BarterBidEntity>
}
