package com.todeal.domain.report.dto

data class ReportProcessRequest(
    val isProcessed: Boolean,
    val adminMemo: String?
)
