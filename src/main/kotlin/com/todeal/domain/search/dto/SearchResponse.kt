package com.todeal.domain.search.dto

data class SearchResponse(
    val id: Long,
    val keyword: String,
    val userId: Long?
)
