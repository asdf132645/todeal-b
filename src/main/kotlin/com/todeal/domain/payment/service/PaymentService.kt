// ✅ PaymentService.kt (엔티티 저장용 공통 서비스)
package com.todeal.domain.payment.service

import com.todeal.domain.payment.entity.PaymentEntity
import com.todeal.domain.payment.repository.PaymentRepository
import org.springframework.stereotype.Service

@Service
class PaymentService(
    private val paymentRepository: PaymentRepository
) {
    fun save(payment: PaymentEntity): PaymentEntity {
        return paymentRepository.save(payment)
    }
}
