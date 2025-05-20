package com.todeal.domain.search.controller

import com.todeal.domain.search.dto.SearchRequest
import com.todeal.domain.search.dto.SearchResponse
import com.todeal.domain.search.service.SearchService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/searches")
class SearchController(
    private val searchService: SearchService
) {

    @PostMapping
    fun save(@RequestBody request: SearchRequest): SearchResponse {
        // ⚠️ userId는 로그인 이후 세션에서 받을 것 (지금은 null 처리)
        return searchService.saveSearch(null, request)
    }

    @GetMapping
    fun getAll(): List<SearchResponse> {
        return searchService.getAll()
    }
}
