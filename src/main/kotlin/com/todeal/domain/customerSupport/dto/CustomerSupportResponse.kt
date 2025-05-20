package com.todeal.domain.customerSupport.dto

import com.todeal.domain.customerSupport.entity.CustomerSupportEntity
import java.time.LocalDateTime

data class CustomerSupportResponse(
    val id: Long,
    val userId: Long,
    val title: String,
    val content: String,
    val createdAt: LocalDateTime,
    val status: String,
    val adminReply: String?
) {
    companion object {
        fun from(entity: CustomerSupportEntity): CustomerSupportResponse {
            return CustomerSupportResponse(
                id = entity.id,
                userId = entity.userId,
                title = entity.title,
                content = entity.content,
                createdAt = entity.createdAt,
                status = entity.status.name,
                adminReply = entity.adminReply
            )
        }
    }
}
