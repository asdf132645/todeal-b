package com.todeal.domain.customerSupport.service

import com.todeal.domain.customerSupport.dto.CustomerSupportRequest
import com.todeal.domain.customerSupport.dto.CustomerSupportResponse
import com.todeal.domain.customerSupport.entity.CustomerSupportEntity
import com.todeal.domain.customerSupport.entity.CustomerSupportEntity.Status
import com.todeal.domain.customerSupport.repository.CustomerSupportRepository
import org.springframework.stereotype.Service

@Service
class CustomerSupportService(
    private val repository: CustomerSupportRepository
) {
    fun submitInquiry(userId: Long, request: CustomerSupportRequest): CustomerSupportResponse {
        val entity = CustomerSupportEntity(
            userId = userId,
            title = request.title,
            content = request.content
        )
        return CustomerSupportResponse.from(repository.save(entity))
    }

    fun getMyInquiries(userId: Long): List<CustomerSupportResponse> {
        return repository.findAllByUserIdOrderByCreatedAtDesc(userId).map(CustomerSupportResponse::from)
    }

    fun getInquiry(id: Long): CustomerSupportResponse {
        val entity = repository.findById(id).orElseThrow { IllegalArgumentException("문의가 존재하지 않습니다.") }
        return CustomerSupportResponse.from(entity)
    }

    fun getAll(): List<CustomerSupportResponse> = repository.findAll().map(CustomerSupportResponse::from)

    fun replyToInquiry(id: Long, reply: String): CustomerSupportResponse {
        val inquiry = repository.findById(id).orElseThrow { IllegalArgumentException("문의가 존재하지 않습니다.") }
        inquiry.status = Status.ANSWERED
        inquiry.adminReply = reply
        return CustomerSupportResponse.from(repository.save(inquiry))
    }
}
