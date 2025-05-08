package com.todeal.domain.hashtag.service

import com.todeal.domain.hashtag.dto.HashtagDto
import com.todeal.domain.hashtag.repository.HashtagRepository
import org.springframework.stereotype.Service

@Service
class HashtagService(
    private val hashtagRepository: HashtagRepository
) {
    fun findAll(): List<HashtagDto> {
        return hashtagRepository.findAll().map { HashtagDto.from(it) }
    }

    fun findByName(name: String): HashtagDto? {
        return hashtagRepository.findByName(name)?.let { HashtagDto.from(it) }
    }

    fun getWeeklyPopularHashtags(limit: Int): List<String> {
        return hashtagRepository.findWeeklyPopularHashtags(limit)
    }

}
