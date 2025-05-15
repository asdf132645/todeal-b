package com.todeal.domain.deal.scheduler

import com.todeal.domain.bid.repository.BidRepository
import com.todeal.domain.chat.repository.ChatRoomRepository
import com.todeal.domain.chat.repository.ChatMessageRepository
import com.todeal.domain.deal.repository.DealRepository
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Component
class DealCleanupScheduler(
    private val dealRepository: DealRepository,
    private val bidRepository: BidRepository,
    private val chatRoomRepository: ChatRoomRepository,
    private val chatMessageRepository: ChatMessageRepository
) {

    @Scheduled(cron = "0 30 3 * * ?") // 매일 새벽 3:30
    @Transactional
    fun deleteExpiredDealsAndRelations() {
        val threshold = LocalDateTime.now().minusDays(7)
        val expiredDeals = dealRepository.findAllByDeadlineBefore(threshold)

        for (deal in expiredDeals) {
            // 🔹 입찰 삭제
            bidRepository.deleteAllByDealId(deal.id)

            // 🔹 채팅방 및 메시지 삭제
            val chatRoom = chatRoomRepository.findByDealId(deal.id)
            if (chatRoom != null) {
                chatMessageRepository.deleteAllByChatRoomId(chatRoom.id)
                chatRoomRepository.delete(chatRoom)
            }
        }

        // 🔹 딜 삭제
        dealRepository.deleteAll(expiredDeals)
        println("🧹 마감 후 7일 지난 딜 ${expiredDeals.size}건 및 연관 데이터 삭제 완료")
    }
}
