package com.todeal.domain.deal.service

import com.todeal.domain.chat.repository.ChatMessageRepository
import com.todeal.domain.chat.repository.ChatRoomRepository
import com.todeal.domain.deal.dto.*
import com.todeal.domain.deal.entity.DealEntity
import com.todeal.domain.deal.repository.DealRepository
import com.todeal.domain.deal.repository.DealQueryRepository
import com.todeal.domain.deal.mapper.toDto
import com.todeal.domain.deal.repository.getByIdOrThrow
import com.todeal.global.response.ApiResponse
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
            pricingType = request.pricingType,
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
            images = request.images,
            translatedTitle = request.translatedTitle,
            translatedContent = request.translatedContent,
            language = request.language
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
        cursor: Long?,
        size: Int,
        lat: Double?,
        lng: Double?,
        radius: Int,
        useLocation: Boolean
    ): List<DealEntity> {
        val (latFilter, lngFilter, radiusFilter) = if (useLocation) Triple(lat, lng, radius) else Triple(null, null, 0)
        return dealQueryRepository.findFilteredDeals(type, hashtags, sort, cursor, size, latFilter, lngFilter, radiusFilter)
    }



    fun searchDealsByTypeAndKeyword(
        type: String,
        keyword: String?,
        exclude: String?,
        pageSize: Int?,        // ✅ nullable → default 적용
        page: Int,
        lat: Double?,
        lng: Double?,
        radius: Int,
        useLocation: Boolean
    ): List<DealResponse> {
        val size = pageSize ?: 20                          // ✅ 기본값 설정
        val offset = (page - 1) * size                     // ✅ pageSize 반영한 offset 계산
        val (latFilter, lngFilter, radiusFilter) = if (useLocation) {
            Triple(lat, lng, radius)
        } else {
            Triple(null, null, 0)
        }

        val deals = dealQueryRepository.searchDealsByTypeAndKeywordWithLocation(
            type = type,
            keyword = keyword,
            exclude = exclude,
            offset = offset,
            limit = size,                                    // ✅ pageSize → limit 파라미터 전달
            lat = latFilter,
            lng = lngFilter,
            radius = radiusFilter
        )

        return deals.map { it.toDto() }
    }

    fun searchDealsByCursor(
        type: String,
        keyword: String?,
        exclude: String?,
        cursorId: Long?,
        pageSize: Int,
        lat: Double?,
        lng: Double?,
        radius: Int,
        useLocation: Boolean
    ): ApiResponse<Map<String, Any?>> {
        val (latFilter, lngFilter, radiusFilter) = if (useLocation) {
            Triple(lat, lng, radius)
        } else {
            Triple(null, null, 0)
        }

        val deals = dealQueryRepository.searchByCursor(
            type = type,
            keyword = keyword,
            exclude = exclude,
            cursorId = cursorId,
            limit = pageSize + 1,  // 다음 페이지 확인용 +1
            lat = latFilter,
            lng = lngFilter,
            radius = radiusFilter
        )

        val hasNext = deals.size > pageSize
        val sliced = if (hasNext) deals.dropLast(1) else deals

        val result: Map<String, Any?> = mapOf(
            "items" to sliced.map { it.toDto() },
            "nextCursorId" to sliced.lastOrNull()?.id,
            "hasNext" to hasNext
        )

        return ApiResponse.success(result)
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
            pricingType = request.pricingType,
            startPrice = request.startPrice,
            deadline = request.deadline,
            region = request.region,
            regionDepth1 = request.regionDepth1,
            regionDepth2 = request.regionDepth2,
            regionDepth3 = request.regionDepth3,
            latitude = request.latitude,
            longitude = request.longitude,
            images = request.images,
            translatedTitle = request.translatedTitle,
            translatedContent = request.translatedContent,
            language = request.language
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
