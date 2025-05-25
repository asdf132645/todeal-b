package com.todeal.domain.translate.controller

import com.todeal.domain.translate.dto.TranslateRequest
import com.todeal.domain.translate.service.TranslationService
import com.todeal.global.response.ApiResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/translate")
class TranslationController(
    private val translationService: TranslationService
) {

    @PostMapping
    fun translate(@RequestBody request: TranslateRequest): ApiResponse<Map<String, Any>> {
        val translatedText = translationService.translate(request)
        val result = mapOf("translatedText" to translatedText)
        return ApiResponse.success(result)
    }
}
