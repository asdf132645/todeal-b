package com.todeal.domain.bid.repository

import com.todeal.domain.bid.entity.BidEntity
import com.todeal.domain.deal.entity.DealEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDateTime

interface BidRepository : JpaRepository<BidEntity, Long> {

    fun findByDealId(dealId: Long): List<BidEntity>

    fun findByUserIdOrderByCreatedAtDesc(userId: Long): List<BidEntity>

    fun findByDealIdIn(dealIds: List<Long>): List<BidEntity>

    fun findByUserId(userId: Long, pageable: Pageable): Page<BidEntity>

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
    fun findByUserIdAndTitleContainingIgnoreCase(userId: Long, keyword: String, pageable: Pageable): Page<DealEntity>

    fun deleteAllByDealId(dealId: Long)

    @Query(
        value = """
        SELECT * FROM deals d
        WHERE d.user_id = :userId
        AND (:keyword IS NULL OR LOWER(CAST(d.title AS TEXT)) LIKE LOWER(CONCAT('%', :keyword, '%')))
        AND EXISTS (
            SELECT 1 FROM bids b WHERE b.deal_id = d.id
        )
        ORDER BY d.created_at DESC
        """,
        countQuery = """
        SELECT COUNT(*) FROM deals d
        WHERE d.user_id = :userId
        AND (:keyword IS NULL OR LOWER(CAST(d.title AS TEXT)) LIKE LOWER(CONCAT('%', :keyword, '%')))
        AND EXISTS (
            SELECT 1 FROM bids b WHERE b.deal_id = d.id
        )
        """,
        nativeQuery = true
    )
    fun findMyDealsWithBidsNative(
        @Param("userId") userId: Long,
        @Param("keyword") keyword: String?,
        pageable: Pageable
    ): Page<DealEntity>


}
