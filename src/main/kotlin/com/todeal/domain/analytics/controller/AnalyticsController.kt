package com.todeal.domain.analytics.controller

import com.todeal.domain.analytics.dto.SearchLogRequest
import com.todeal.domain.analytics.dto.VisitorLogRequest
import com.todeal.domain.analytics.service.VisitorLogService
import com.todeal.domain.analytics.service.SearchLogService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/analytics")
class AnalyticsController(
    private val visitorLogService: VisitorLogService,
    private val searchLogService: SearchLogService
) {
    @PostMapping("/visit")
    fun logVisit(
        @RequestBody request: VisitorLogRequest,
        httpRequest: HttpServletRequest
    ) {
        val userId = httpRequest.getAttribute("userId") as? Long
        visitorLogService.logVisit(request, userId)
    }

    @PostMapping("/search")
    fun logSearch(
        @RequestBody request: SearchLogRequest,
        httpRequest: HttpServletRequest
    ) {
        val userId = httpRequest.getAttribute("userId") as? Long
        searchLogService.logSearch(request, userId)
    }

    @GetMapping("/top-searches")
    fun topSearches(): List<Map<String, Any>> {
        return searchLogService.getTopKeywords()
            .map { mapOf("keyword" to it.first, "count" to it.second) }
    }
}
