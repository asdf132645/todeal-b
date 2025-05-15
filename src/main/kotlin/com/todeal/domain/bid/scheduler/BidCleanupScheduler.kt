package com.todeal.domain.bid.scheduler
//
//import com.todeal.domain.bid.repository.BidRepository
//import org.springframework.scheduling.annotation.Scheduled
//import org.springframework.stereotype.Component
//import org.springframework.transaction.annotation.Transactional
//import java.time.LocalDateTime
//
//@Component
//class BidCleanupScheduler(
//    private val bidRepository: BidRepository
//) {
//
//    @Scheduled(cron = "0 0 3 * * ?") // 매일 새벽 3시
//    @Transactional
//    fun deleteExpiredUnwonBids() {
//        val threshold = LocalDateTime.now().minusDays(30)
//        val bidsToDelete = bidRepository.findUnwonBefore(threshold)
//        bidRepository.deleteAll(bidsToDelete)
//        println("🧹 30일 경과된 미낙찰 입찰 ${bidsToDelete.size}건 삭제됨")
//    }
//}
