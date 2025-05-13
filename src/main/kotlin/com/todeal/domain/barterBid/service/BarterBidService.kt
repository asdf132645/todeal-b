package com.todeal.domain.barterBid.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.todeal.domain.barterBid.dto.BarterBidRequest
import com.todeal.domain.barterBid.dto.BarterBidResponse
import com.todeal.domain.barterBid.entity.BarterBidEntity
import com.todeal.domain.barterBid.repository.BarterBidRepository
import com.todeal.domain.deal.repository.DealRepository
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BarterBidService(
    private val barterBidRepository: BarterBidRepository,
    private val dealRepository: DealRepository,
    private val redisTemplate: RedisTemplate<String, String>
) {

    private val objectMapper = jacksonObjectMapper()

    @Transactional
    fun createBarterBid(userId: Long, request: BarterBidRequest): BarterBidResponse {
        val deal = dealRepository.findById(request.dealId).orElseThrow { RuntimeException("딜이 존재하지 않음") }

        val bid = BarterBidEntity(
            dealId = request.dealId,
            userId = userId,
            proposedItem = request.proposedItem,
            description = request.description,
            images = request.images
        )
        val saved = barterBidRepository.save(bid)

        // ✅ Redis Pub/Sub 알림 전송
        val payload = mapOf(
            "type" to "deal",
            "dealId" to deal.id,
            "dealTitle" to deal.title,
            "toUserId" to deal.userId
        )
        redisTemplate.convertAndSend("pubsub:barter:new", objectMapper.writeValueAsString(payload))

        return BarterBidResponse.fromEntity(saved)
    }

    fun getBarterBidsByDeal(dealId: Long): List<BarterBidResponse> {
        return barterBidRepository.findByDealId(dealId)
            .map { BarterBidResponse.fromEntity(it) }
    }

    @Transactional
    fun acceptBid(id: Long) {
        val acceptedBid = barterBidRepository.findById(id)
            .orElseThrow { RuntimeException("입찰이 존재하지 않음") }

        // 1. 해당 입찰 수락
        acceptedBid.status = BarterBidEntity.BarterBidStatus.ACCEPTED
        barterBidRepository.save(acceptedBid)

        // 2. 동일 딜의 다른 입찰 모두 거절
        val otherBids = barterBidRepository.findByDealId(acceptedBid.dealId)
            .filter { it.id != acceptedBid.id && it.status == BarterBidEntity.BarterBidStatus.PENDING }

        otherBids.forEach {
            it.status = BarterBidEntity.BarterBidStatus.REJECTED
        }

        barterBidRepository.saveAll(otherBids)
    }

    @Transactional
    fun rejectBid(id: Long) {
        val bid = barterBidRepository.findById(id).orElseThrow { RuntimeException("입찰이 존재하지 않음") }
        bid.status = BarterBidEntity.BarterBidStatus.REJECTED
        barterBidRepository.save(bid)
    }
}