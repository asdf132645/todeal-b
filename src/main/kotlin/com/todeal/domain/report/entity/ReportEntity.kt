package com.todeal.domain.report.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "report", uniqueConstraints = [UniqueConstraint(columnNames = ["fromUserId", "toUserId", "dealId"])])
data class ReportEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false) val fromUserId: Long,
    @Column(nullable = false) val toUserId: Long,
    @Column val dealId: Long? = null,
    @Column(nullable = false, length = 100) val reason: String,
    @Column(columnDefinition = "TEXT") val detail: String? = null,
    @Column(nullable = false) val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false) var isProcessed: Boolean = false,
    @Column var processedAt: LocalDateTime? = null,
    @Column(columnDefinition = "TEXT") var adminMemo: String? = null
)
