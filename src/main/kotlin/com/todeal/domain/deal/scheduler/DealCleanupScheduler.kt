package com.todeal.domain.deal.scheduler

import com.todeal.domain.bid.repository.BidRepository
import com.todeal.domain.chat.repository.ChatRoomRepository
import com.todeal.domain.chat.repository.ChatMessageRepository
import com.todeal.domain.deal.entity.DealStatus
import com.todeal.domain.deal.repository.DealRepository
import com.todeal.domain.user.repository.UserRepository
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Component
class DealCleanupScheduler(
    private val dealRepository: DealRepository,
    private val bidRepository: BidRepository,
    private val chatRoomRepository: ChatRoomRepository,
    private val chatMessageRepository: ChatMessageRepository,
    private val userRepository: UserRepository // ✅ 추가됨
) {

    @Scheduled(cron = "0 30 3 * * ?") // 매일 새벽 3:30
    @Transactional
    fun markExpiredDealsOnly() {
        val now = LocalDateTime.now()

        // 🔹 1. 마감 시간이 지난 딜 중 ACTIVE 상태인 것
        val expiredDeals = dealRepository.findAllByDeadlineBeforeAndStatus(now, DealStatus.ACTIVE)

        // 🔹 2. 신고 10회 이상 유저의 딜 중 ACTIVE 상태인 것
        val reportedUserIds = userRepository.findAllByReportCountGreaterThanEqual(10).map { it.id }
        val reportedUserDeals = if (reportedUserIds.isNotEmpty()) {
            dealRepository.findAllByUserIdInAndStatus(reportedUserIds, DealStatus.ACTIVE)
        } else {
            emptyList()
        }

        // 🔹 중복 제거
        val allToExpire = (expiredDeals + reportedUserDeals).distinctBy { it.id }

        for (deal in allToExpire) {
            deal.status = DealStatus.EXPIRED
        }

        println("🕒 상태 마감 처리된 딜: ${allToExpire.size}건 (마감시간 초과 + 신고 누적 유저)")
    }
}
