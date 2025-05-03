package com.todeal.domain.search.service

import com.todeal.domain.search.dto.SearchRequest
import com.todeal.domain.search.dto.SearchResponse
import com.todeal.domain.search.entity.SearchEntity
import com.todeal.domain.search.repository.SearchRepository
import org.springframework.stereotype.Service

@Service
class SearchService(
    private val searchRepository: SearchRepository
) {

    fun saveSearch(userId: Long?, request: SearchRequest): SearchResponse {
        val saved = searchRepository.save(
            SearchEntity(keyword = request.keyword, userId = userId)
        )
        return SearchResponse(saved.id, saved.keyword, saved.userId)
    }

    fun getAll(): List<SearchResponse> {
        return searchRepository.findAll().map {
            SearchResponse(it.id, it.keyword, it.userId)
        }
    }
}
