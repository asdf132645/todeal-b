package com.todeal.global.response

data class CursorResponse<T>(
    val items: List<T>,
    val nextCursorId: Long?,
    val nextCursorCreatedAt: String?,
    val hasNext: Boolean
)
