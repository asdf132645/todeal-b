package com.todeal.domain.deal.mapper

import com.todeal.domain.deal.dto.DealDto
import com.todeal.domain.deal.dto.DealInternalDto
import com.todeal.domain.deal.dto.DealResponse
import com.todeal.domain.deal.entity.DealEntity
import java.time.LocalDateTime
import java.time.ZoneId

// ✅ 프론트 JSON 응답용 Map
fun DealEntity.toResponse(): Map<String, Any> {
    val result = mutableMapOf<String, Any>(
        "id" to id,
        "title" to title,
        "description" to description,
        "type" to type,
        "pricingType" to pricingType.name,
        "startPrice" to startPrice,
        "currentPrice" to currentPrice,
        "deadline" to deadline,
        "userId" to userId,
        "region" to region,
        "regionDepth1" to regionDepth1,
        "regionDepth2" to regionDepth2,
        "regionDepth3" to regionDepth3,
        "latitude" to latitude,
        "longitude" to longitude,
        "images" to images,
        "createdAt" to createdAt,
        "updatedAt" to updatedAt,
        "isExpired" to (deadline.isBefore(LocalDateTime.now()) || winnerBidId != null),
        "cursor" to createdAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() // ✅ 추가
    )

    winnerBidId?.let { result["winnerBidId"] = it }
    translatedTitle?.let { result["translatedTitle"] = it }
    translatedContent?.let { result["translatedContent"] = it }
    language?.let { result["language"] = it }

    return result
}


// ✅ DTO 변환용 (Service → Controller 응답)
fun DealEntity.toDto(): DealResponse {
    return DealResponse(
        id = this.id,
        title = this.title,
        description = this.description,
        type = this.type,
        pricingType = this.pricingType,
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
        translatedTitle = this.translatedTitle,
        translatedContent = this.translatedContent,
        language = this.language,
        cursor = this.createdAt.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli() // 추가
    )
}

// ✅ Service 내부 로직용 DTO
fun DealEntity.toServiceDto(): DealInternalDto {
    return DealInternalDto(
        id = this.id,
        title = this.title,
        description = this.description,
        type = this.type,
        pricingType = this.pricingType,
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
        translatedTitle = this.translatedTitle,
        translatedContent = this.translatedContent,
        language = this.language
    )
}
