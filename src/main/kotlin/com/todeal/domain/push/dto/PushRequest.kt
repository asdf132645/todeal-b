package com.todeal.domain.push.dto

data class PushRequest(
    val userId: Long,
    val title: String,
    val body: String
)
