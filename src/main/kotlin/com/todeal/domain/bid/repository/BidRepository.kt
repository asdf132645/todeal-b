package com.todeal.domain.bid.repository

import com.todeal.domain.bid.entity.BidEntity
import org.springframework.data.jpa.repository.JpaRepository

interface BidRepository : JpaRepository<BidEntity, Long> {
    fun findTopByDealIdOrderByAmountDesc(dealId: Long): BidEntity?
}
