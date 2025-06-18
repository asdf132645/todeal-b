package com.todeal.domain.deal.dto

import com.todeal.domain.deal.model.PricingType
import java.time.LocalDateTime

data class DealRequest(
    val title: String,
    val description: String,
    val type: String,
    val pricingType: PricingType = PricingType.BIDDING,
    val startPrice: Int,
    val deadline: LocalDateTime,
    val region: String,
    val regionDepth1: String,
    val regionDepth2: String,
    val regionDepth3: String,
    val latitude: Double,
    val longitude: Double,
    val images: List<String> = emptyList(),
    val translatedTitle: String? = null,
    val translatedContent: String? = null,
    val language: String? = null
)
