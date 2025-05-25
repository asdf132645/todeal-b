package com.todeal.domain.deal.dto

data class PromoteRequest(
    val days: Long // 예: 3일간 노출 → now + 3일로 만료시간 계산
)
