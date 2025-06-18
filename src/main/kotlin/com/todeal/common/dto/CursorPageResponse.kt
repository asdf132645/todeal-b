// src/common/dto/CursorPageResponse.kt
package com.todeal.common.dto

data class CursorPageResponse<T>(
    val items: List<T>,
    val nextCursor: Long?
)
