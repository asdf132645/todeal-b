package com.todeal.domain.bid.service

import com.todeal.domain.bid.dto.BidDto
import com.todeal.domain.bid.dto.BidRequest
import com.todeal.domain.bid.dto.BidResponse
import com.todeal.domain.bid.entity.BidEntity
import com.todeal.domain.bid.repository.BidRepository
import org.springframework.stereotype.Service

@Service
class BidService(
    private val bidRepository: BidRepository
) {

    fun placeBid(request: BidRequest): BidResponse {
        val bid = BidEntity(
            dealId = request.dealId,
            userId = request.userId,
            amount = request.amount
        )
        bidRepository.save(bid)
        return BidResponse(success = true, message = "입찰 성공")
    }

    fun getHighestBid(dealId: Long): BidDto? {
        return bidRepository.findTopByDealIdOrderByAmountDesc(dealId)?.let { BidDto.from(it) }
    }
}
