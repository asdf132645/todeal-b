package com.todeal.domain.deal.dto

import com.todeal.domain.deal.entity.DealEntity
import java.time.LocalDateTime

data class DealDto(
    val id: Long,
    val title: String,
    val description: String,
    val type: String,
    val startPrice: Int,
    val currentPrice: Int,
    val deadline: LocalDateTime,
    val images: List<String>
) {
    companion object {
        fun from(entity: DealEntity) = DealDto(
            id = entity.id,
            title = entity.title,
            description = entity.description,
            type = entity.type,
            startPrice = entity.startPrice,
            currentPrice = entity.currentPrice,
            deadline = entity.deadline,
            images = entity.images
        )
    }
}
