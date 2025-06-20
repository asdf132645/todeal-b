package com.todeal.domain.analytics.service

import com.todeal.domain.analytics.dto.SearchLogRequest
import com.todeal.domain.analytics.entity.SearchLogEntity
import com.todeal.domain.analytics.repository.SearchLogRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class SearchLogService(
    private val searchLogRepository: SearchLogRepository
) {
    fun logSearch(req: SearchLogRequest, userId: Long?) {
        val entity = SearchLogEntity(
            keyword = req.keyword.trim().lowercase(),
            userId = userId
        )
        searchLogRepository.save(entity)
    }

    fun getTopKeywords(sinceDays: Long = 7, limit: Int = 10): List<Pair<String, Long>> {
        val since = LocalDateTime.now().minusDays(sinceDays)
        return searchLogRepository.findTopKeywords(since, limit).map {
            (it["keyword"] as String) to (it["count"] as Number).toLong()
        }
    }
}
