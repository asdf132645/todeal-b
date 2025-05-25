package com.todeal.domain.translate.dto

data class TranslateRequest(
    val source: String,
    val target: String,
    val text: String
)
