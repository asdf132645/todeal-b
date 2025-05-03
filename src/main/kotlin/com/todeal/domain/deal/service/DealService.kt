package com.todeal.domain.deal.service

import com.todeal.domain.deal.dto.*
import com.todeal.domain.deal.entity.DealEntity
import com.todeal.domain.deal.repository.DealRepository
import org.springframework.stereotype.Service

@Service
class DealService(
    private val dealRepository: DealRepository
) {

    fun createDeal(request: DealRequest): DealDto {
        val deal = DealEntity(
            title = request.title,
            description = request.description,
            type = request.type,
            startPrice = request.startPrice,
            currentPrice = request.startPrice,
            deadline = request.deadline,
            images = request.images
        )
        return DealDto.from(dealRepository.save(deal))
    }

    fun getDealById(id: Long): DealDto {
        val deal = dealRepository.findById(id).orElseThrow { NoSuchElementException("Deal not found") }
        return DealDto.from(deal)
    }

    fun getAllDeals(): List<DealResponse> {
        return dealRepository.findAll().map {
            DealResponse(
                id = it.id,
                title = it.title,
                type = it.type,
                currentPrice = it.currentPrice
            )
        }
    }
}
