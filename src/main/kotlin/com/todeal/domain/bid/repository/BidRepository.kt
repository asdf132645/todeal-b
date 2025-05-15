package com.todeal.domain.bid.repository

import com.todeal.domain.bid.entity.BidEntity
import org.springframework.data.repository.query.Param
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.domain.Page
import java.time.LocalDateTime


interface BidRepository : JpaRepository<BidEntity, Long> {

    fun findByDealId(dealId: Long): List<BidEntity>

    fun findByUserIdOrderByCreatedAtDesc(userId: Long): List<BidEntity>

    fun findByDealIdIn(dealIds: List<Long>): List<BidEntity>
    // ✅ 페이징용
    fun findByUserId(userId: Long, pageable: Pageable): Page<BidEntity>

    // ✅ 딜 타입, 제목 검색 포함 (deal join 필요)
    @Query(
        """
        SELECT b FROM BidEntity b
        JOIN DealEntity d ON b.dealId = d.id
        WHERE b.userId = :userId
        AND (:type IS NULL OR d.type = :type)
        AND (:keyword IS NULL OR d.title ILIKE %:keyword%)
        """
    )
    fun searchMyBids(
        userId: Long,
        type: String?,
        keyword: String?,
        pageable: Pageable
    ): Page<BidEntity>

    @Query(
        """
    SELECT b FROM BidEntity b 
    JOIN DealEntity d ON b.dealId = d.id 
    WHERE d.winnerBidId IS NULL AND d.deadline < :threshold
    """
    )
    fun findUnwonBefore(@Param("threshold") threshold: LocalDateTime): List<BidEntity>
    fun deleteAllByDealId(dealId: Long)

}
