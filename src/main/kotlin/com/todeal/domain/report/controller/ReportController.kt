package com.todeal.domain.report.controller

import com.todeal.domain.report.dto.ReportRequest
import com.todeal.domain.report.service.ReportService
import com.todeal.global.response.ApiResponse
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/reports")
class ReportController(
    private val reportService: ReportService
) {

    @PostMapping("/submit")
    fun submitReport(
        @RequestHeader("X-USER-ID") fromUserId: Long,
        @RequestBody request: ReportRequest
    ): ApiResponse<String> {
        reportService.submitReport(fromUserId, request)
        return ApiResponse.success("신고가 접수되었습니다.")
    }
}
