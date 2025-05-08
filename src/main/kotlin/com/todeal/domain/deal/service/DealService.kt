// ✅ DealService.kt
package com.todeal.domain.deal.service

import com.todeal.domain.deal.dto.*
import com.todeal.domain.deal.entity.DealEntity
import com.todeal.domain.deal.repository.DealRepository
import com.todeal.domain.deal.repository.DealQueryRepository
import org.springframework.stereotype.Service

@Service
class DealService(
    private val dealRepository: DealRepository,
    private val dealQueryRepository: DealQueryRepository
) {

    fun createDeal(request: DealRequest): DealDto {
        val deal = DealEntity(
            title = request.title,
            description = request.description,
            type = request.type,
            startPrice = request.startPrice,
            currentPrice = request.startPrice,
            deadline = request.deadline,
            latitude = request.latitude,
            longitude = request.longitude,
            images = request.images
        )
        return DealDto.from(dealRepository.save(deal))
    }

    fun getDealById(id: Long): DealDto {
        val deal = dealRepository.findById(id).orElseThrow { NoSuchElementException("Deal not found") }
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
}
