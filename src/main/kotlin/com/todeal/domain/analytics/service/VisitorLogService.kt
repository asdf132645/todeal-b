package com.todeal.domain.analytics.service

import com.todeal.domain.analytics.dto.VisitorLogRequest
import com.todeal.domain.analytics.entity.VisitorLogEntity
import com.todeal.domain.analytics.repository.VisitorLogRepository
import org.springframework.stereotype.Service
import jakarta.servlet.http.HttpServletRequest

@Service
class VisitorLogService(
    private val visitorLogRepository: VisitorLogRepository,
    private val request: HttpServletRequest
) {
    fun logVisit(req: VisitorLogRequest, userId: Long?) {
        val ip = request.remoteAddr
        val entity = VisitorLogEntity(
            ip = ip,
            path = req.path,
            userAgent = req.userAgent,
            userId = userId
        )
        visitorLogRepository.save(entity)
    }
}
