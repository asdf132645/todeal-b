package com.todeal.domain.analytics.dto

data class VisitorLogRequest(
    val path: String,
    val userAgent: String
)
