package com.todeal.domain.report.dto

import java.time.LocalDateTime

data class ReportAdminResponse(
    val id: Long,
    val fromUserId: Long,
    val toUserId: Long,
    val dealId: Long?,
    val reason: String,
    val detail: String?,
    val isProcessed: Boolean,
    val processedAt: LocalDateTime?,
    val adminMemo: String?,
    val createdAt: LocalDateTime
)
