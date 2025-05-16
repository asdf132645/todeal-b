package com.todeal.domain.report.dto

data class ReportRequest(
    val toUserId: Long,
    val dealId: Long?,             // optional: 딜과 관련된 신고면 포함
    val reason: String,            // 선택지: 욕설/사기/허위정보 등
    val detail: String? = null     // 추가 설명 (선택)
)
