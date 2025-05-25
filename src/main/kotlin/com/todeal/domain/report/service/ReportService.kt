package com.todeal.domain.report.service

import com.todeal.domain.report.dto.ReportRequest
import com.todeal.domain.report.entity.ReportEntity
import com.todeal.domain.report.repository.ReportRepository
import com.todeal.domain.user.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ReportService(
    private val reportRepository: ReportRepository,
    private val userRepository: UserRepository  // ✅ 추가
) {

    @Transactional
    fun submitReport(fromUserId: Long, request: ReportRequest) {
        if (fromUserId == request.toUserId) {
            throw IllegalArgumentException("자기 자신은 신고할 수 없습니다.")
        }

        if (reportRepository.existsByFromUserIdAndToUserIdAndDealId(fromUserId, request.toUserId, request.dealId)) {
            throw IllegalStateException("이미 해당 사용자에 대해 신고를 접수하였습니다.")
        }

        val report = ReportEntity(
            fromUserId = fromUserId,
            toUserId = request.toUserId,
            dealId = request.dealId,
            reason = request.reason,
            detail = request.detail
        )

        reportRepository.save(report)

        val reportedUser = userRepository.findById(request.toUserId)
            .orElseThrow { IllegalArgumentException("신고 대상 유저가 존재하지 않습니다.") }

        reportedUser.reportCount += 1
    }
}
