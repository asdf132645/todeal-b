package com.todeal.domain.deal.dto

import com.todeal.domain.deal.entity.DealEntity

data class DealDto(
    val id: Long,
    val title: String,
    val description: String,
    val type: String,
    val startPrice: Int,
    val currentPrice: Int,
    val deadline: String,
    val region: String,
    val regionDepth1: String,
    val regionDepth2: String,
    val regionDepth3: String,
    val latitude: Double,
    val longitude: Double,
    val images: List<String>,
    val createdAt: String,
    val updatedAt: String
) {
    companion object {
        fun from(entity: DealEntity): DealDto {
            return DealDto(
                id = entity.id,
                title = entity.title,
                description = entity.description,
                type = entity.type,
                startPrice = entity.startPrice,
                currentPrice = entity.currentPrice,
                deadline = entity.deadline.toString(),
                region = entity.region,
                regionDepth1 = entity.regionDepth1,
                regionDepth2 = entity.regionDepth2,
                regionDepth3 = entity.regionDepth3,
                latitude = entity.latitude,
                longitude = entity.longitude,
                images = entity.images,
                createdAt = entity.createdAt.toString(),
                updatedAt = entity.updatedAt.toString()
            )
        }
    }
}
