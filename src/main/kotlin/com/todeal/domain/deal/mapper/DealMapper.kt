package com.todeal.domain.deal.mapper

import com.todeal.domain.deal.dto.DealDto
import com.todeal.domain.deal.dto.DealInternalDto
import com.todeal.domain.deal.entity.DealEntity
import com.todeal.domain.deal.dto.DealResponse
import java.time.LocalDateTime

// ✅ 프론트 JSON 응답용 Map
fun DealEntity.toResponse(): Map<String, Any> {
    val result = mutableMapOf<String, Any>(
        "id" to id,
        "title" to title,
        "description" to description,
        "type" to type,
        "startPrice" to startPrice,
        "currentPrice" to currentPrice,
        "deadline" to deadline,
        "userId" to userId, // ✅ 추가
        "region" to region,
        "regionDepth1" to regionDepth1,
        "regionDepth2" to regionDepth2,
        "regionDepth3" to regionDepth3,
        "latitude" to latitude,
        "longitude" to longitude,
        "images" to images,
        "createdAt" to createdAt,
        "updatedAt" to updatedAt,
        "isExpired" to (deadline.isBefore(LocalDateTime.now()) || winnerBidId != null) // ✅ 핵심
    )

    winnerBidId?.let {
        result["winnerBidId"] = it
    }

    return result
}

// ✅ DTO 변환용 (Service 내부, 응답 객체)
fun DealEntity.toDto(): DealResponse {
    return DealResponse(
        id = this.id,
        title = this.title,
        description = this.description,
        type = this.type,
        startPrice = this.startPrice,
        currentPrice = this.currentPrice,
        deadline = this.deadline,
        region = this.region,
        regionDepth1 = this.regionDepth1,
        regionDepth2 = this.regionDepth2,
        regionDepth3 = this.regionDepth3,
        latitude = this.latitude,
        longitude = this.longitude,
        images = this.images,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
        winnerBidId = this.winnerBidId
    )
}

fun DealEntity.toServiceDto(): DealInternalDto {
    return DealInternalDto(
        id = this.id,
        title = this.title,
        description = this.description,
        type = this.type,
        startPrice = this.startPrice,
        currentPrice = this.currentPrice,
        deadline = this.deadline,
        region = this.region,
        regionDepth1 = this.regionDepth1,
        regionDepth2 = this.regionDepth2,
        regionDepth3 = this.regionDepth3,
        latitude = this.latitude,
        longitude = this.longitude,
        images = this.images,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
        winnerBidId = this.winnerBidId,
        ownerId = this.userId,
    )
}
