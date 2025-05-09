package com.todeal.domain.deal.service

import com.todeal.domain.deal.dto.*
import com.todeal.domain.deal.entity.DealEntity
import com.todeal.domain.deal.repository.DealRepository
import com.todeal.domain.deal.repository.DealQueryRepository
import org.springframework.stereotype.Service
import com.todeal.domain.deal.mapper.toDto


@Service
class DealService(
    private val dealRepository: DealRepository,
    private val dealQueryRepository: DealQueryRepository
) {

    fun createDeal(userId: Long, request: DealRequest): DealDto {
        val deal = DealEntity(
            title = request.title,
            description = request.description,
            type = request.type,
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
        lng: Double?
    ): List<DealEntity> {
        return dealQueryRepository.findFilteredDeals(type, hashtags, sort, page, size, lat, lng)
    }

    /**
     * 🔍 제목 기반 키워드 검색 + 타입 필터링
     */
    fun searchDealsByTypeAndKeyword(
        type: String,
        keyword: String?,
        page: Int
    ): List<DealResponse> {
        val offset = (page - 1) * 20
        val deals = if (keyword.isNullOrBlank()) {
            dealRepository.findByType(type, offset)
        } else {
            dealRepository.findByTypeAndKeyword(type, keyword, offset)
        }
        return deals.map { it.toDto() }
    }
}
