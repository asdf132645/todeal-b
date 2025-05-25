package com.todeal.domain.hashtag.service

import com.todeal.domain.hashtag.dto.HashtagDto
import com.todeal.domain.hashtag.repository.DealHashtagRepository
import com.todeal.domain.hashtag.repository.HashtagRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
class HashtagService(
    private val hashtagRepository: HashtagRepository,
    private val dealHashtagRepository: DealHashtagRepository
) {
    fun findAll(limit: Int): List<HashtagDto> {
        return hashtagRepository.findAll(PageRequest.of(0, limit))
            .content
            .map { HashtagDto(it.id, it.name) }
    }


    fun findByName(name: String): HashtagDto? {
        return hashtagRepository.findByName(name)?.let {
            HashtagDto(it.id, it.name)
        }
    }

    fun getWeeklyPopularHashtags(limit: Int): List<String> {
        return dealHashtagRepository.findPopularHashtags(PageRequest.of(0, limit))
    }
}
