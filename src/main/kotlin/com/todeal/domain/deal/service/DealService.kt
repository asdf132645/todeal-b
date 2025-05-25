package com.todeal.domain.deal.service

import com.todeal.domain.chat.repository.ChatMessageRepository
import com.todeal.domain.chat.repository.ChatRoomRepository
import com.todeal.domain.deal.dto.*
import com.todeal.domain.deal.entity.DealEntity
import com.todeal.domain.deal.repository.DealRepository
import com.todeal.domain.deal.repository.DealQueryRepository
import com.todeal.domain.deal.mapper.toDto
import com.todeal.domain.deal.repository.getByIdOrThrow
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class DealService(
    private val dealRepository: DealRepository,
    private val dealQueryRepository: DealQueryRepository,
    private val chatRoomRepository: ChatRoomRepository,
    private val chatMessageRepository: ChatMessageRepository
) {

    fun createDeal(userId: Long, request: DealRequest): DealDto {
        val deal = DealEntity(
            title = request.title,
            description = request.description,
            type = request.type,
            pricingType = request.pricingType, // ✅ 여기에 추가
            userId = userId,
            startPrice = request.startPrice,
            currentPrice = request.startPrice,
            deadline = request.deadline,
            region = request.region,
            regionDepth1 = request.regionDepth1,
            regionDepth2 = request.regionDepth2,
            regionDepth3 = request.regionDepth3,
            latitude = request.latitude,
            longitude = request.longitude,
            images = request.images
        )
        return DealDto.from(dealRepository.save(deal))
    }


    fun getDealById(id: Long): DealDto {
        val deal = dealRepository.findById(id)
            .orElseThrow { NoSuchElementException("Deal not found") }
        return DealDto.from(deal)
    }

    fun getFilteredDeals(
        type: String?,
        hashtags: List<String>?,
        sort: String,
        page: Int,
        size: Int,
        lat: Double?,
        lng: Double?,
        radius: Int
    ): List<DealEntity> {
        return dealQueryRepository.findFilteredDeals(type, hashtags, sort, page, size, lat, lng, radius)
    }

    fun searchDealsByTypeAndKeyword(
        type: String,
        keyword: String?,
        exclude: String?,
        page: Int
    ): List<DealResponse> {
        val offset = (page - 1) * 20

        val deals = when {
            keyword.isNullOrBlank() && exclude.isNullOrBlank() -> {
                dealRepository.findByType(type, offset)
            }
            keyword.isNullOrBlank() && !exclude.isNullOrBlank() -> {
                dealRepository.findByTypeExcludingKeyword(type, exclude, offset)
            }
            !keyword.isNullOrBlank() && exclude.isNullOrBlank() -> {
                dealRepository.findByTypeAndKeyword(type, keyword, offset)
            }
            else -> {
                dealRepository.findByTypeAndKeywordExcluding(type, keyword!!, exclude!!, offset)
            }
        }

        return deals.map { it.toDto() }
    }

    @Transactional
    fun deleteDealWithChats(userId: Long, dealId: Long) {
        val deal = dealRepository.findById(dealId)
            .orElseThrow { IllegalArgumentException("존재하지 않는 딜입니다") }

        if (deal.userId != userId) throw IllegalAccessException("삭제 권한이 없습니다")

        val chatRooms = chatRoomRepository.findAllByDealId(dealId)
        chatRooms.forEach { room ->
            chatMessageRepository.deleteByChatRoomId(room.id)
        }
        chatRoomRepository.deleteAll(chatRooms)

        dealRepository.delete(deal)
    }

    fun getDealsByUserId(userId: Long): List<DealResponse> {
        val results = dealRepository.findByUserId(userId)
        return results.map { it.toDto() }
    }

    @Transactional
    fun updateDeal(userId: Long, dealId: Long, request: DealRequest): DealDto {
        val deal = dealRepository.getByIdOrThrow(dealId)

        if (deal.userId != userId) {
            throw IllegalAccessException("수정 권한이 없습니다.")
        }

        deal.update(
            title = request.title,
            description = request.description,
            type = request.type,
            pricingType = request.pricingType, // ✅ 여기에 추가
            startPrice = request.startPrice,
            deadline = request.deadline,
            region = request.region,
            regionDepth1 = request.regionDepth1,
            regionDepth2 = request.regionDepth2,
            regionDepth3 = request.regionDepth3,
            latitude = request.latitude,
            longitude = request.longitude,
            images = request.images
        )

        return DealDto.from(deal)
    }

    @Transactional
    fun promoteDeal(userId: Long, dealId: Long, days: Long) {
        val deal = dealRepository.getByIdOrThrow(dealId)

        if (deal.userId != userId) {
            throw IllegalAccessException("본인의 딜만 프로모션 등록할 수 있습니다.")
        }

        deal.isPromoted = true
        deal.promotionExpireAt = LocalDateTime.now().plusDays(days)
    }
}
