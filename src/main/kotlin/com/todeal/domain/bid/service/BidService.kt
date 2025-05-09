package com.todeal.domain.bid.service

import com.todeal.domain.bid.dto.*
import com.todeal.domain.bid.entity.BidEntity
import com.todeal.domain.bid.repository.BidRepository
import com.todeal.domain.deal.mapper.toDto
import com.todeal.domain.deal.repository.DealRepository
import com.todeal.domain.deal.repository.getByIdOrThrow
import com.todeal.domain.user.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BidService(
    private val bidRepository: BidRepository,
    private val userRepository: UserRepository,
    private val dealRepository: DealRepository
) {

    fun getBidsByDealId(dealId: Long): List<BidResponse> {
        val deal = dealRepository.getByIdOrThrow(dealId)
        val bids = bidRepository.findByDealId(dealId)
        val userIds = bids.map { it.userId }.toSet()
        val userMap = userRepository.findByIdIn(userIds).associateBy { it.id }

        return bids.map {
            val nickname = userMap[it.userId]?.nickname ?: "알 수 없음"
            BidResponse.fromEntity(it, nickname, deal.winnerBidId)
        }
    }

    @Transactional
    fun selectWinnerBid(bidId: Long) {
        val bid = bidRepository.findById(bidId).orElseThrow { RuntimeException("입찰 없음") }
        val deal = dealRepository.getByIdOrThrow(bid.dealId)
        deal.winnerBidId = bidId
    }

    /** 🔥 입찰 등록 */
    @Transactional
    fun placeBid(request: BidRequest) {
        val user = userRepository.findByNickname(request.nickname)
            ?: throw RuntimeException("해당 닉네임의 유저가 존재하지 않습니다.")

        val deal = dealRepository.getByIdOrThrow(request.dealId)

        val bid = BidEntity(
            dealId = request.dealId,
            userId = user.id,
            amount = request.amount
        )
        bidRepository.save(bid)

        if (request.amount > deal.currentPrice) {
            deal.currentPrice = request.amount
        }
    }

    fun getMyBids(userId: Long): List<BidWithDealDto> {
        val bids = bidRepository.findByUserIdOrderByCreatedAtDesc(userId)
        val dealIds = bids.map { it.dealId }.toSet()
        val dealMap = dealRepository.findByIdIn(dealIds).associateBy { it.id }
        val user = userRepository.findById(userId).orElse(null)
        val nickname = user?.nickname ?: "알 수 없음"

        return bids.mapNotNull { bid ->
            val deal = dealMap[bid.dealId] ?: return@mapNotNull null
            BidWithDealDto.from(bid, nickname, deal.toDto())
        }
    }

    fun getBidsOnMyDeals(userId: Long): List<DealBidGroupDto> {
        val myDeals = dealRepository.findByUserId(userId)
        val dealIds = myDeals.map { it.id }
        val bids = bidRepository.findByDealIdIn(dealIds)
        val userIds = bids.map { it.userId }.toSet()
        val userMap = userRepository.findByIdIn(userIds).associateBy { it.id }

        return myDeals.mapNotNull { deal ->
            val bidsOnDeal = bids.filter { it.dealId == deal.id }
            if (bidsOnDeal.isEmpty()) return@mapNotNull null

            DealBidGroupDto(
                deal = deal.toDto(),
                bids = bidsOnDeal.map { bid ->
                    val nickname = userMap[bid.userId]?.nickname ?: "알 수 없음"
                    BidResponse.fromEntity(bid, nickname, deal.winnerBidId)
                }
            )
        }
    }
}
