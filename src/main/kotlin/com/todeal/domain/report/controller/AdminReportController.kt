package com.todeal.domain.report.controller

import com.todeal.domain.report.dto.ReportAdminResponse
import com.todeal.domain.report.dto.ReportProcessRequest
import com.todeal.domain.report.repository.ReportRepository
import com.todeal.global.response.ApiResponse
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/admin/reports")
class AdminReportController(
    private val reportRepository: ReportRepository
) {

    @GetMapping
    fun getAllReports(): ApiResponse<List<ReportAdminResponse>> {
        val reports = reportRepository.findAll()
            .sortedByDescending { it.createdAt }
            .map {
                ReportAdminResponse(
                    id = it.id,
                    fromUserId = it.fromUserId,
                    toUserId = it.toUserId,
                    dealId = it.dealId,
                    reason = it.reason,
                    detail = it.detail,
                    isProcessed = it.isProcessed,
                    processedAt = it.processedAt,
                    adminMemo = it.adminMemo,
                    createdAt = it.createdAt
                )
            }
        return ApiResponse.success(reports)
    }

    @PatchMapping("/{id}")
    fun processReport(
        @PathVariable id: Long,
        @RequestBody request: ReportProcessRequest
    ): ApiResponse<String> {
        val report = reportRepository.findById(id)
            .orElseThrow { IllegalArgumentException("해당 신고 내역이 존재하지 않습니다.") }

        report.isProcessed = request.isProcessed
        report.adminMemo = request.adminMemo
        report.processedAt = if (request.isProcessed) LocalDateTime.now() else null

        reportRepository.save(report)
        return ApiResponse.success("신고 처리 상태가 업데이트되었습니다.")
    }
}
