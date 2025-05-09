// ✅ 파일 위치: com.todeal.domain.bid.dto.DealBidGroupDto.kt
package com.todeal.domain.bid.dto

import com.todeal.domain.deal.dto.DealResponse

data class DealBidGroupDto(
    val deal: DealResponse,
    val bids: List<BidResponse>
)
