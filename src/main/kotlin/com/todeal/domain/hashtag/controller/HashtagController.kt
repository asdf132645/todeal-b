package com.todeal.domain.hashtag.controller

import com.todeal.domain.hashtag.dto.HashtagDto
import com.todeal.domain.hashtag.service.HashtagService
import com.todeal.global.response.ApiResponse
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/hashtags")
class HashtagController(
    private val hashtagService: HashtagService
) {

    @GetMapping
    fun getAll(
        @RequestParam(defaultValue = "50") limit: Int
    ): ApiResponse<List<HashtagDto>> {
        val result = hashtagService.findAll(limit)
        return ApiResponse.success(result)
    }


    @GetMapping("/{name}")
    fun getByName(@PathVariable name: String): ApiResponse<HashtagDto?> {
        val result = hashtagService.findByName(name)
        return ApiResponse.success(result)
    }

    @GetMapping("/popular/week")
    fun getWeeklyPopularHashtags(
        @RequestParam(defaultValue = "10") limit: Int
    ): ApiResponse<List<String>> {
        val result = hashtagService.getWeeklyPopularHashtags(limit)
        return ApiResponse.success(result)
    }
}
