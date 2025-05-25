package com.todeal.domain.bid.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.todeal.domain.barterBid.repository.BarterBidRepository
import com.todeal.domain.bid.dto.*
import com.todeal.domain.bid.entity.BidEntity
import com.todeal.domain.bid.repository.BidRepository
import com.todeal.domain.deal.entity.DealEntity
import com.todeal.domain.deal.mapper.toDto
import com.todeal.domain.deal.mapper.toServiceDto
import com.todeal.domain.deal.repository.DealRepository
import com.todeal.domain.deal.repository.getByIdOrThrow
import com.todeal.domain.user.repository.UserRepository
import org.springframework.data.domain.*
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class BidService(
    private val bidRepository: BidRepository,
    private val barterBidRepository: BarterBidRepository,
    private val userRepository: UserRepository,
    private val dealRepository: DealRepository,
    private val redisTemplate: RedisTemplate<String, String>
) {

    private val objectMapper = jacksonObjectMapper()

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
        dealRepository.save(deal)
    }

    @Transactional
    fun cancelWinner(dealId: Long) {
        val deal = dealRepository.getByIdOrThrow(dealId)
        deal.winnerBidId = null
    }

    @Transactional
    fun placeBid(request: BidRequest) {
        val user = userRepository.findByNickname(request.nickname)
            ?: throw IllegalArgumentException("존재하지 않는 닉네임입니다.")

        val deal = dealRepository.getByIdOrThrow(request.dealId)

        val bid = BidEntity(
            dealId = request.dealId,
            userId = user.id,
            amount = request.amount
        )
        bidRepository.save(bid)

        if (request.amount > deal.currentPrice) {
            deal.currentPrice = request.amount
            dealRepository.save(deal) // 꼭 저장
        }

        val payload = mapOf(
            "type" to "deal",
            "dealId" to deal.id,
            "dealTitle" to deal.title,
            "amount" to request.amount,
            "bidderNickname" to request.nickname,
            "toUserId" to deal.userId
        )

        println("🚨 입찰 알림 Redis 전송: $payload")
        redisTemplate.convertAndSend("pubsub:bid:new", objectMapper.writeValueAsString(payload))
    }


    fun getMyBids(userId: Long, type: String?, keyword: String?, page: Int, size: Int): Page<Any> {
        val pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))
        val nickname = userRepository.findById(userId).orElse(null)?.nickname ?: "알 수 없음"

        val bidList = if (type == null || type == "used" || type == "parttime" || type == "parttime-request") {
            bidRepository.searchMyBids(userId, type, keyword, Pageable.unpaged()).content
        } else emptyList()

        val barterList = if (type == null || type == "barter") {
            barterBidRepository.findByUserId(userId)
        } else emptyList()

        val allDealIds = (bidList.map { it.dealId } + barterList.map { it.dealId }).toSet()
        val dealMap: Map<Long, DealEntity> = dealRepository.findByIdIn(allDealIds).associateBy { it.id }

        val bidDtos = bidList.mapNotNull { bid ->
            val deal = dealMap[bid.dealId]?.toServiceDto() ?: return@mapNotNull null
            BidWithDealDto.from(bid, nickname, deal)
        }

        val barterDtos = barterList.mapNotNull { entity ->
            val deal = dealMap[entity.dealId]?.toServiceDto() ?: return@mapNotNull null
            BarterBidWithDealDto.from(entity, deal)
        }

        val combined = (bidDtos + barterDtos).sortedByDescending {
            when (it) {
                is BidWithDealDto -> it.createdAt
                is BarterBidWithDealDto -> it.createdAt
                else -> LocalDateTime.MIN
            }
        }

        val start = page * size
        val end = (start + size).coerceAtMost(combined.size)
        val paged = if (start < end) combined.subList(start, end) else emptyList()

        return PageImpl(paged, pageable, combined.size.toLong())
    }

    fun getBidsOnMyDeals(userId: Long, page: Int, size: Int, keyword: String?): Page<DealBidGroupDto> {
        val pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))

        // 1. 내가 등록한 딜 중 검색어 필터링
        val myDealsPage = if (!keyword.isNullOrBlank()) {
            dealRepository.findByUserIdAndTitleContainingIgnoreCase(userId, keyword, pageable)
        } else {
            dealRepository.findByUserId(userId, pageable)
        }

        // 2. 딜 ID 목록
        val dealIds = myDealsPage.content.map { it.id }

        // 3. 해당 딜들의 입찰 목록
        val bids = bidRepository.findByDealIdIn(dealIds)
        val userIds = bids.map { it.userId }.toSet()
        val userMap = userRepository.findByIdIn(userIds).associateBy { it.id }

        // 4. 입찰이 존재하는 딜만 그룹핑
        val grouped = myDealsPage.content.mapNotNull { deal ->
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

        return PageImpl(grouped, pageable, myDealsPage.totalElements)
    }


    @Transactional
    fun cancelBid(bidId: Long) {
        val bid = bidRepository.findById(bidId)
            .orElseThrow { IllegalArgumentException("존재하지 않는 입찰입니다") }

        bidRepository.delete(bid)
        bidRepository.flush() // ✅ 강제로 DB 반영
    }
}