package com.todeal.domain.report.scheduler

import com.todeal.domain.report.repository.ReportRepository
import com.todeal.domain.user.repository.UserRepository
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class ReportAutoBanScheduler(
    private val reportRepository: ReportRepository,
    private val userRepository: UserRepository
) {
    val THRESHOLD = 10L // ← Long 리터럴로 바꿔줘야 함

    @Scheduled(cron = "0 */10 * * * *") // 매 10분마다
    fun autoBanReportedUsers() {
        println("🚨 [AutoBan] 신고 누적 사용자 자동 정지 검사 시작: ${LocalDateTime.now()}")

        val flaggedUserIds = reportRepository.findUserIdsWithReportCountOver(THRESHOLD)

        flaggedUserIds.forEach { userId ->
            val user = userRepository.findById(userId).orElse(null) ?: return@forEach
            if (!user.isBanned) {
                user.isBanned = true
                user.banReason = "신고 누적 ${THRESHOLD}회 이상으로 자동 정지됨"
                println("⛔ [AutoBan] 유저 $userId 정지 처리 완료")
            }
        }
    }
}
